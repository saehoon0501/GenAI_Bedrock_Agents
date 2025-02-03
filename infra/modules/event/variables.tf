variable "event_queue_arn" {
  type        = string
  description = "The ARN of the SQS queue to attach to the EventBridge rule"
}

variable "tags" {
  type        = map(string)
  description = "The tags of the event"
}