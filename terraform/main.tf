terraform {
  required_version = ">= 1.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 4.0"
    }
  }
  backend "gcs" {
    bucket = "writeshite-terraform-state"
    prefix = "terraform/state"
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
}

resource "google_service_account" "cloudbuild_service_account" {
  account_id   = "build-bot"
  display_name = "Cloud Build Service Account"
}

resource "google_project_iam_member" "cloudbuild_roles" {
  for_each = toset([
    "roles/cloudbuild.builds.editor",
    "roles/run.admin",
    "roles/dns.admin",
    "roles/logging.admin",
    "roles/logging.logWriter",
    "roles/storage.admin",
    "roles/storage.objectAdmin",
    "roles/iam.serviceAccountUser",
  ])

  project = var.project_id
  role    = each.value
  member  = google_service_account.cloudbuild_service_account.email

  depends_on = [google_service_account.cloudbuild_service_account]
}

resource "google_cloud_run_service" "writeshite_backend" {
  name     = "writeshite-backend"
  location = var.region

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/writeshite-backend:latest"
        ports {
          container_port = 8080
        }
      }
      service_account_name = google_service_account.cloudbuild_service_account.email
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }
}

resource "google_cloud_run_service_iam_member" "writeshite_backend_public_access" {
  service  = google_cloud_run_service.writeshite_backend.name
  location = google_cloud_run_service.writeshite_backend.location
  role     = "roles/run.invoker"
  member   = "allUsers"

  depends_on = [google_cloud_run_service.writeshite_backend]
}

resource "google_dns_managed_zone" "writeshite_zone" {
  name        = "writeshite-zone"
  dns_name    = "writeshite.com."
  description = "DNS zone for writeshite.com"
}

resource "google_cloud_run_domain_mapping" "writeshite_backend_domain_mapping" {
  location = var.region
  name     = "writeshite.com"

  metadata {
    namespace = var.project_id
  }

  spec {
    route_name = google_cloud_run_service.writeshite_backend.name
  }

  depends_on = [google_dns_managed_zone.writeshite_zone]
}

resource "google_cloud_run_domain_mapping" "writeshite_backend_www_domain_mapping" {
  location = var.region
  name     = "www.writeshite.com"

  metadata {
    namespace = var.project_id
  }

  spec {
    route_name = google_cloud_run_service.writeshite_backend.name
  }

  depends_on = [google_dns_managed_zone.writeshite_zone]
}

resource "google_cloud_run_domain_mapping" "writeshite_backend_cert" {
  location = var.region
  name     = "writeshite-backend-cert"

  metadata {
    namespace = var.project_id
    annotations = {
      "run.googleapis.com/managed-certificates" = "writeshite-backend-cert"
    }
  }

  spec {
    route_name = google_cloud_run_service.writeshite_backend.name
  }

  depends_on = [google_dns_managed_zone.writeshite_zone]
}

resource "google_storage_bucket" "writeshite_frontend" {
  name          = "writeshite-frontend"
  location      = var.region
  force_destroy = true
}

resource "google_storage_bucket_object" "writeshite_frontend_files" {
  name   = "index.html"
  bucket = google_storage_bucket.writeshite_frontend.name
  source = "src/frontend/build/index.html"

  depends_on = [google_storage_bucket.writeshite_frontend]
}

resource "google_dns_record_set" "writeshite_frontend_apex" {
  name         = "writeshite.com."
  type         = "A"
  ttl          = 300
  managed_zone = google_dns_managed_zone.writeshite_zone.name
  rrdatas      = [google_cloud_run_domain_mapping.writeshite_backend_domain_mapping.status[0].resource_records[0]]

  depends_on = [google_dns_managed_zone.writeshite_zone, google_cloud_run_domain_mapping.writeshite_backend_domain_mapping]
}

resource "google_dns_record_set" "writeshite_frontend_www" {
  name         = "www.writeshite.com."
  type         = "CNAME"
  ttl          = 300
  managed_zone = google_dns_managed_zone.writeshite_zone.name
  rrdatas      = ["writeshite.com."]

  depends_on = [google_dns_managed_zone.writeshite_zone]
}