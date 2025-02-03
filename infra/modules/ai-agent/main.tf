data "aws_iam_policy_document" "agent_trust" {
    statement {
        actions = ["sts:AssumeRole"]
        principals {
            type = "Service"
            identifiers = ["bedrock.amazonaws.com"]
        }
    }
}

resource "aws_iam_role" "agent_role" {
    assume_role_policy = data.aws_iam_policy_document.agent_trust.json
    description = "IAM role for Bedrock Agent"
    name = "ai-agent-role"    
}

data "aws_iam_policy_document" "agent_policy_document" {
    statement {
        sid     = "BedrockFoundationModels"
        effect = "Allow"
        actions = [
            "bedrock:InvokeModel", 
            "bedrock:InvokeModelWithResponseStream", 
            "bedrock:ListFoundationModels", 
            "bedrock:DescribeModel", 
            "bedrock:ListCustomModels", 
            "bedrock:DescribeCustomModel"
        ]
        resources = ["*"]
    }

    statement {
    sid    = "S3Access"
    effect = "Allow"
    actions = [
      "s3:GetObject",
      "s3:PutObject"
    ]
    resources = [
      var.s3_bucket_arn
    ]
  }

    statement {
      sid    = "LambdaInvoke"
      effect = "Allow"
      actions = [
        "lambda:InvokeFunction"
      ]     
      resources = [
        var.lambda_function_arn
      ]
    }
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

resource "aws_bedrockagent_agent" "agent" {
    agent_name = "${var.project_name}-agent"    
    agent_resource_role_arn = aws_iam_role.agent_role.arn
    idle_session_ttl_in_seconds = 3600    
    foundation_model = var.foundation_model 
    instruction = var.instruction    
    prompt_override_configuration {     
        prompt_configurations{
          base_prompt_template = var.orchestration_prompt_template          
          parser_mode = "DEFAULT"
          prompt_creation_mode = "OVERRIDDEN"
          prompt_state = "ENABLED"
          prompt_type = "ORCHESTRATION"
          inference_configuration {
            max_length = var.orchestration_inference_configuration.maximumLength
            stop_sequences = var.orchestration_inference_configuration.stopSequences
            temperature = var.orchestration_inference_configuration.temperature
            top_p = var.orchestration_inference_configuration.topP
            top_k = var.orchestration_inference_configuration.topK
          }
        }
    }

    tags = var.tags
}

resource "aws_bedrockagent_agent_alias" "agent_alias" {
  agent_alias_name = "${var.project_name}-agent-${var.environment}"
  agent_id = aws_bedrockagent_agent.agent.id
  description = "Alias for the ${var.environment} agent"
}

