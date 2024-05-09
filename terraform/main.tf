terraform {
  required_version = ">= 1.0"
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }
}

provider "google" {
  project = "peaceful-app-379819"
  region  = "us-central1"
}

resource "google_storage_bucket" "artifact_bucket" {
  name     = "writeshite-artifacts"
  location = "us-central1"
}

resource "google_cloud_run_service" "backend" {
  name     = "writeshite-backend"
  location = "us-central1"

  template {
    spec {
      containers {
        image = "gcr.io/peaceful-app-379819/writeshite-backend:latest"
        env {
          name  = "ENV_VARIABLE_NAME"
          value = "ENV_VARIABLE_VALUE"
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }
}

resource "google_storage_bucket" "frontend_bucket" {
  name     = "whiteshite-frontend-bucket"
  location = "us-central1"

  uniform_bucket_level_access = true

  website {
    main_page_suffix = "index.html"
    not_found_page   = "index.html"
  }
}

resource "google_cloud_run_domain_mapping" "backend_domain_mapping" {
  location = "us-central1"
  name     = "writeshite.com"

  metadata {
    namespace = "peaceful-app-379819"
  }

  spec {
    route_name = google_cloud_run_service.backend.name
  }
}

resource "google_storage_bucket_iam_member" "frontend_domain_mapping" {
  bucket = google_storage_bucket.frontend_bucket.name
  role   = "roles/storage.objectViewer"
  member = "allUsers"
}