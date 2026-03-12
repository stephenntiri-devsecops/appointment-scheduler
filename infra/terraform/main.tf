terraform {
  required_version = ">= 1.6.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# --- VPC (placeholder) ---
# module "vpc" { ... }

# --- EKS (placeholder) ---
# module "eks" { ... }

# --- Aurora PostgreSQL (placeholder) ---
# module "aurora" { ... }

# --- ElastiCache Redis (optional placeholder) ---
# module "redis" { ... }

# --- SQS + DLQ (example) ---
resource "aws_sqs_queue" "notifications_dlq" {
  name                      = "${var.name}-notifications-dlq"
  message_retention_seconds = 1209600
}

resource "aws_sqs_queue" "notifications_queue" {
  name = "${var.name}-notifications"
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.notifications_dlq.arn
    maxReceiveCount     = 5
  })
}

# --- ECR example ---
resource "aws_ecr_repository" "appointments" {
  name = "${var.name}/appointments-service"
  image_scanning_configuration { scan_on_push = true }
}

resource "aws_ecr_repository" "notifications" {
  name = "${var.name}/notifications-service"
  image_scanning_configuration { scan_on_push = true }
}
