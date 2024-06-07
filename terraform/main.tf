terraform {
  required_version = "~> 1.7.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "5.32.0"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

resource "google_service_account" "service_account" {
  account_id   = "writeshite-service-account"
  display_name = "WriteShite Service Account"
}

resource "google_storage_bucket" "frontend_bucket" {
  name     = "writeshite-frontend"
  location = var.region
  project  = var.project_id

  website {
    main_page_suffix = "index.html"
    not_found_page   = "404.html"
  }
  cors {
    origin = ["https://writeshite.com", "https://www.writeshite.com"]
    method = ["GET", "HEAD", "PUT", "POST", "DELETE"]
    response_header = ["*"]
    max_age_seconds = 3600
  }

  lifecycle {
    ignore_changes = [location, id]
  }
}

resource "google_storage_bucket" "writeshite-tfstate" {
  name     = "writeshite-tfstate"
  location = var.region
  project  = var.project_id
}

resource "google_storage_bucket" "artifact_bucket" {
  name     = "writeshite-artifacts"
  location = var.region
  project  = var.project_id
}

resource "google_project_iam_member" "service_account_roles" {
  project = var.project_id
  role    = each.value
  member  = "serviceAccount:${google_service_account.service_account.email}"

  for_each = toset([
    "roles/storage.admin",
    "roles/dns.admin",
    "roles/run.admin",
    "roles/cloudbuild.builds.editor",
  ])
}

resource "google_compute_instance_template" "writeshite_backend_template" {
  name        = "writeshite-backend-template"
  machine_type = "e2-micro"

  tags = ["http-server"]

  disk {
    auto_delete  = true
    boot         = true
    source_image = "projects/debian-cloud/global/images/family/debian-10"
  }

  network_interface {
    network = google_compute_network.default.name
    access_config {}
  }

  metadata = {
    gce-container-declaration = <<-EOF
    spec:
      containers:
        - name: writeshite-backend
          image: gcr.io/${var.project_id}/writeshite-backend:latest
          ports:
            - name: http
              containerPort: 8080
      restartPolicy: Always
    EOF
  }
}

resource "google_compute_instance_group_manager" "writeshite_backend_instance_group" {
  name               = "writeshite-backend-group"
  base_instance_name = "writeshite-backend"
  zone               = "us-central1-a"
  target_size        = 1

  version {
    instance_template = google_compute_instance_template.writeshite_backend_template.self_link
  }
}

resource "google_compute_backend_service" "backend_service" {
  name        = "writeshite-backend-service"
  protocol    = "HTTP"
  port_name   = "http"
  timeout_sec = 10

  backend {
    group = google_compute_instance_group_manager.writeshite_backend_instance_group.instance_group
  }

  health_checks = [google_compute_http_health_check.writeshite-health-check.self_link]
}

resource "google_compute_backend_bucket" "frontend_bucket" {
  name        = "writeshite-frontend-backend-bucket"
  bucket_name = google_storage_bucket.frontend_bucket.name
}

resource "google_compute_url_map" "writeshite-url-map" {
  name            = "writeshite-url-map"
  default_service = google_compute_backend_service.backend_service.id

  host_rule {
    hosts        = ["writeshite.com", "www.writeshite.com"]
    path_matcher = "allpaths"
  }

  path_matcher {
    name            = "allpaths"
    default_service = google_compute_backend_service.backend_service.id

    path_rule {
      paths   = ["/api/*"]
      service = google_compute_backend_service.backend_service.id
    }

    path_rule {
      paths   = ["/*"]
      service = google_compute_backend_bucket.frontend_bucket.self_link
    }
  }
}

resource "google_compute_http_health_check" "writeshite-health-check" {
  name                = "writeshite-health-check"
  request_path        = "/health"
  check_interval_sec  = 5
  timeout_sec         = 5
  healthy_threshold   = 2
  unhealthy_threshold = 2
}

resource "google_compute_address" "writeshite-com" {
  name       = "writeshite-com"
  region     = var.region
  address_type = "EXTERNAL"
}

resource "google_compute_network" "default" {
  auto_create_subnetworks                   = true
  delete_default_routes_on_create           = false
  description                               = "Default network for the project"
  enable_ula_internal_ipv6                  = false
  mtu                                       = 0
  name                                      = "default"
  network_firewall_policy_enforcement_order = "AFTER_CLASSIC_FIREWALL"
  project                                   = var.project_id
  routing_mode                              = "REGIONAL"
}

resource "google_compute_subnetwork" "default" {
  ip_cidr_range              = "10.128.0.0/20"
  name                       = "default"
  network                    = "default"
  private_ip_google_access   = false
  private_ipv6_google_access = "DISABLE_GOOGLE_ACCESS"
  project                    = var.project_id
  purpose                    = "PRIVATE"
  region                     = var.region
  secondary_ip_range         = []
  stack_type                 = "IPV4_ONLY"
}
