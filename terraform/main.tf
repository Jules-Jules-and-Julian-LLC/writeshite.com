terraform {
  required_version = "~> 1.7.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "4.9.0"
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

resource "google_cloud_run_service" "backend" {
  name     = "writeshite-backend"
  location = var.region

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/writeshite-backend:latest"
      }
    }
  }
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
      service = google_storage_bucket.frontend_bucket.url
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

resource "google_compute_backend_service" "backend_service" {
  name        = "writeshite-backend-service"
  protocol    = "HTTP"
  port_name   = "http"
  timeout_sec = 10

  backend {
    group = google_compute_instance_group.backend_instance_group.self_link
  }

  health_checks = [google_compute_http_health_check.writeshite-health-check.self_link]
}

resource "google_compute_instance_group" "backend_instance_group" {
  name        = "writeshite-backend-group"
  zone        = "us-central1-a"
  instances   = [google_compute_instance.backend_instance.self_link]
}

resource "google_compute_instance" "backend_instance" {
  name         = "writeshite-instance"
  machine_type = "e2-micro"
  zone         = "us-central1-a"

  boot_disk {
    initialize_params {
      image = "projects/peaceful-app-379819/global/writeshite-backend:latest"
    }
  }

  network_interface {
    network = "default"

    access_config {
      nat_ip = google_compute_address.writeshite-com.address
    }
  }
}

resource "google_compute_address" "writeshite-com" {
  name       = "writeshite-com"
  region     = var.region
  address_type = "EXTERNAL"
}

resource "google_compute_image" "writeshite-backend" {
  name = "writeshite-backend"
}