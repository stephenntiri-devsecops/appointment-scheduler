output "notifications_queue_url" {
  value = aws_sqs_queue.notifications_queue.url
}
