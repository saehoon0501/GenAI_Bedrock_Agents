output "queue_arn" {
  value = aws_sqs_queue.event_queue.arn
}

output "queue_url" {
  value = aws_sqs_queue.event_queue.id
}

output "queue_name" {
  value = aws_sqs_queue.event_queue.name
}