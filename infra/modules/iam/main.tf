data "aws_caller_identity" "current" {}

data "aws_partition" "current" {}

data "aws_region" "current" {}

data "aws_iam_policy_document" "agent_trust" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      identifiers = ["bedrock.amazonaws.com"]
      type        = "Service"
    }
    condition {
      test     = "StringEquals"
      values   = [data.aws_caller_identity.current.account_id]
      variable = "aws:SourceAccount"
    }
    condition {
      test     = "ArnLike"
      values   = ["arn:${data.aws_partition.current.partition}:bedrock:${data.aws_region.current.name}:${data.aws_caller_identity.current.account_id}:agent/*"]
      variable = "AWS:SourceArn"
    }
  }
}

data "aws_iam_policy_document" "agent_policy_document" {
    statement {
        sid     = "BedrockFoundationModels"
        effect = "Allow"
        actions = [
            "bedrock:InvokeModel", 
            "bedrock:GetAgentAlias",
            "bedrock:InvokeModelWithResponseStream", 
            "bedrock:ListFoundationModels", 
            "bedrock:DescribeModel", 
            "bedrock:ListCustomModels", 
            "bedrock:DescribeCustomModel"
        ]
        resources = ["*"]
    }
}

resource "aws_iam_role" "agent_role" {
    assume_role_policy = data.aws_iam_policy_document.agent_trust.json
    description = "IAM role for Bedrock Agent"
    name = "ai-agent-role"
    tags = var.tags
}

resource "aws_iam_policy" "agent_policy" {
    name = "ai-agent-policy"
    description = "Policy for Bedrock Agent"
    policy = data.aws_iam_policy_document.agent_policy_document.json
}

resource "aws_iam_role_policy_attachment" "agent_policy_attachment" {
    role = aws_iam_role.agent_role.name
    policy_arn = aws_iam_policy.agent_policy.arn
}