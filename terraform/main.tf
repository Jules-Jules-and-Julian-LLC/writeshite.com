terraform {
  required_version = "~> 1.8.0"
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

resource "google_storage_bucket" "writeshite-frontend" {
  name          = "writeshite-frontend"
  location      = "us-central1"

  website {
    main_page_suffix = "index.html"
    not_found_page   = "404.html"
  }
  cors {
    origin          = ["https://writeshite.com", "https://www.writeshite.com",
      "http://writeshite.com", "http://www.writeshite.com"]
    method          = ["GET", "HEAD", "PUT", "POST", "DELETE"]
    response_header = ["*"]
    max_age_seconds = 3600
  }
}

resource "google_project_iam_member" "service_account_roles" {
  project = var.project_id
  role    = each.value
  member  = "serviceAccount:${google_service_account.service_account.email}"

  for_each = toset([
    "roles/storage.admin",
    "roles/bucket.admin",
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

resource "google_storage_bucket" "frontend" {
  name          = "writeshite-frontend"
  location      = var.region
  force_destroy = true

  uniform_bucket_level_access = true

  website {
    main_page_suffix = "index.html"
    not_found_page   = "404.html"
  }
}

resource "google_dns_managed_zone" "writeshite_zone" {
  name        = "writeshite-zone"
  dns_name    = "writeshite.com."
  description = "DNS zone for writeshite.com"
}

resource "google_dns_record_set" "writeshite_record" {
  name = "www.${google_dns_managed_zone.writeshite_zone.dns_name}"
  type = "CNAME"
  ttl  = 300

  managed_zone = google_dns_managed_zone.writeshite_zone.name

  rrdatas = [google_cloud_run_service.backend.status[0].url]
}