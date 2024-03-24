provider "google" {
  project = "peaceful-app-379819"
  region  = "us-central1"
}

resource "google_cloud_run_service" "writeshite_backend" {
  name     = "writeshite-backend"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/${var.project_id}/writeshite-backend:${var.app_version}"
        ports {
          container_port = 8080
        }
      }
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
}

resource "google_cloud_run_domain_mapping" "writeshite_backend_domain_mapping" {
  location = "us-central1"
  name     = "writeshite.com"

  metadata {
    namespace = "peaceful-app-379819"
  }

  spec {
    route_name = google_cloud_run_service.writeshite_backend.name
  }
}

resource "google_cloud_run_domain_mapping" "writeshite_backend_www_domain_mapping" {
  location = "us-central1"
  name     = "www.writeshite.com"

  metadata {
    namespace = "peaceful-app-379819"
  }

  spec {
    route_name = google_cloud_run_service.writeshite_backend.name
  }
}

resource "google_cloud_run_domain_mapping" "writeshite_backend_cert" {
  location = "us-central1"
  name     = "writeshite-backend-cert"

  metadata {
    namespace = "peaceful-app-379819"
    annotations = {
      "run.googleapis.com/managed-certificates" = "writeshite-backend-cert"
    }
  }

  spec {
    route_name = google_cloud_run_service.writeshite_backend.name
  }
}

resource "google_storage_bucket" "writeshite_frontend" {
  name          = "writeshite-frontend"
  location      = "US"
  force_destroy = true
}

resource "google_storage_bucket_object" "writeshite_frontend_files" {
  name   = "index.html"
  bucket = google_storage_bucket.writeshite_frontend.name
  source = "src/frontend/build/index.html"
}

resource "google_dns_managed_zone" "writeshite_zone" {
  name        = "writeshite-zone"
  dns_name    = "writeshite.com."
  description = "DNS zone for writeshite.com"
}

resource "google_dns_record_set" "writeshite_frontend_apex" {
  name         = "writeshite.com."
  type         = "A"
  ttl          = 300
  managed_zone = google_dns_managed_zone.writeshite_zone.name
  rrdatas      = [google_storage_bucket.writeshite_frontend.url]
}

resource "google_dns_record_set" "writeshite_frontend_www" {
  name         = "www.writeshite.com."
  type         = "CNAME"
  ttl          = 300
  managed_zone = google_dns_managed_zone.writeshite_zone.name
  rrdatas      = [google_storage_bucket.writeshite_frontend.url]
}