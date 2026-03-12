# Temporal on EKS (Helm) — Notes

Temporal provides **durable workflows and timers** so reminders are not missed during deploys or restarts.

## Recommended install approach
- Use the official Temporal Helm chart (community supported) and deploy into `temporal` namespace.
- Back temporal persistence with **Aurora PostgreSQL** (or RDS Postgres).
- Enable metrics export for Prometheus.

## This folder
- `values.example.yaml` — starting point values file (edit for your RDS endpoint, creds via secrets/IRSA)
- `namespace.yaml` — temporal namespace manifest

## Example commands
```bash
kubectl apply -f namespace.yaml
# helm repo add temporal https://charts.temporal.io
# helm upgrade --install temporal temporal/temporal -n temporal -f values.example.yaml
```
