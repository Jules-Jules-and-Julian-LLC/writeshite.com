terraform {
  required_version = "~> 1.7.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
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
  name     = "whiteshite-frontend"
  location = var.region
  project = var.project_id

  website {
    main_page_suffix = "index.html"
    not_found_page   = "404.html"
  }
  cors {
    origin = [
      "https://writeshite.com", "https://www.writeshite.com",
      "http://writeshite.com", "http://www.writeshite.com", "writeshite.com", "www.writeshite.com"
    ]
    method          = ["GET", "HEAD", "PUT", "POST", "DELETE"]
    response_header = ["*"]
    max_age_seconds = 3600
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

#Networking stuff already done by hand
#resource "google_dns_managed_zone" "writeshite_zone" {
#  name        = "writeshite-zone"
#  dns_name    = "writeshite.com."
#  description = "DNS zone for writeshite.com"
#}
#
#resource "google_dns_record_set" "writeshite_record" {
#  name = "www.${google_dns_managed_zone.writeshite_zone.dns_name}"
#  type = "CNAME"
#  ttl  = 300
#
#  managed_zone = google_dns_managed_zone.writeshite_zone.name
#
#  rrdatas = ["writeshite.com"]
#}
#
#resource "google_storage_bucket_iam_member" "frontend_domain_mapping" {
#  bucket = google_storage_bucket.frontend_bucket.id
#  member = "allUsers"
#  role   = "roles/storage.objectViewer"
#}
#
#resource google_cloud_run_domain_mapping "backend_domain_mapping" {
#  location = var.region
#  project = var.project_id
#  name = "writeshite.com"
#  spec {
#    route_name = google_cloud_run_service.backend.name
#  }
#  metadata {
#    namespace = var.project_id
#  }
#}
#
#resource "google_storage_bucket_iam_member" "backend_domain_mapping" {
#  bucket = google_storage_bucket.artifact_bucket.id
#  member = "allUsers"
#  role   = "roles/storage.objectViewer"
#}