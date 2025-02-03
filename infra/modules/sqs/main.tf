##################################
# Create an SQS Queue
##################################
resource "aws_sqs_queue" "event_queue" {
  name = "my-cron-queue"

  tags = var.tags
}

##################################
# Queue Policy to Allow EventBridge to Send Messages
##################################
data "aws_iam_policy_document" "sqs_event_policy" {
  statement {
    sid = "AllowEventBridgeToSendMessage"
    effect = "Allow"

    # The service principal for EventBridge
    principals {
      type        = "Service"
      identifiers = ["events.amazonaws.com"]
    }

    actions   = ["sqs:SendMessage"]
    resources = [      
      aws_sqs_queue.event_queue.arn
    ]
  }
}

resource "aws_sqs_queue_policy" "event_queue_policy" {
  queue_url = aws_sqs_queue.event_queue.id
  policy    = data.aws_iam_policy_document.sqs_event_policy.json
}

