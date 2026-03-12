# Terraform scaffolding (AWS)

This is intentionally a **safe scaffold**:
- VPC, EKS, and core dependencies
- You must review IAM policies, CIDRs, and environment settings before production.

Suggested modules (use terraform-aws-modules):
- VPC: terraform-aws-modules/vpc/aws
- EKS: terraform-aws-modules/eks/aws
- RDS/Aurora: terraform-aws-modules/rds-aurora/aws
- ElastiCache: terraform-aws-modules/elasticache/aws
- SQS: aws_sqs_queue
- KMS: aws_kms_key
- WAF: aws_wafv2_web_acl + association to ALB

Files:
- `main.tf` (module wiring placeholders)
- `variables.tf`
- `outputs.tf`
