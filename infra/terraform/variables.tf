variable "name" {
  type        = string
  description = "Prefix/name for resources"
  default     = "scheduler"
}

variable "aws_region" {
  type        = string
  description = "AWS region"
  default     = "us-east-1"
}
