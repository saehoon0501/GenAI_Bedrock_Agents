locals {
  project_name = "dexter-ai-agent"
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.0.0"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = "3.0.2"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  assume_role {
    role_arn = "arn:aws:iam::975050279870:role/dexter-terraform-execution"
    session_name = "terraform-session"
  }
  region  = "ap-northeast-2"
}

data "aws_region" "current" {}

resource "aws_resourcegroups_group" "resource_group" {
  name = "${local.project_name}-resource-group"
  resource_query {
    query = <<JSON
      {
        "ResourceTypeFilters": ["AWS::AllSupported"],
        "TagFilters": [{"Key": "${local.project_name}-env", "Values": ["${local.project_name}-resource"]}]
      }
    JSON
  }
}

module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  version = ">= 5.0.0"

  name = "${local.project_name}-vpc"
  cidr = "10.0.0.0/16"
  azs = ["ap-northeast-2a", "ap-northeast-2b", "ap-northeast-2c"]
  private_subnets = ["10.0.1.0/24"]
  public_subnets = ["10.0.101.0/24"]

  tags = {    
    "${local.project_name}-env" = "${local.project_name}-resource"
  }
}

module "s3_bucket" {
  source = "./modules/s3"
  project_name = local.project_name
  tags = {
    Name = "my-s3-bucket" 
    "${local.project_name}-env" = "${local.project_name}-resource"
  }
}
module "agent_iam" {
  source = "./modules/iam"  
  tags = {
    Name = "my-iam"
    "${local.project_name}-env" = "${local.project_name}-resource"
  }
}

module "ai_agent_supervisor" {
  source = "./modules/ai-agent"
  project_name = local.project_name
  foundation_model = var.foundation_model
  instruction = var.instruction_supervisor
  agent_role_arn = module.agent_iam.agent_role_arn
  agent_role_name = "supervisor"
  prepare_agent = false
  custom_prompt_configuration = {
    "default" = {
      prompt_configurations = var.orchestration_prompt_configurations
    }
  }
  environment = "dev"
  agent_collaboration = "SUPERVISOR"
  tags = {
    Name = "my-ai-agent-supervisor"
    "${local.project_name}-env" = "${local.project_name}-resource"
  }
  depends_on = [
    module.ai_agent_web_search,
    module.ai_agent_writer
  ]
}

module "ai_agent_web_search" {
  source = "./modules/ai-agent"
  project_name = local.project_name
  foundation_model = var.foundation_model
  instruction = var.instruction_web_search
  agent_role_arn = module.agent_iam.agent_role_arn
  agent_role_name = "web-search"  
  environment = "dev"  
  tags = {
    Name = "my-ai-agent-web-search"
    "${local.project_name}-env" = "${local.project_name}-resource"
  }
}

module "ai_agent_writer" {
  source = "./modules/ai-agent"
  project_name = local.project_name
  foundation_model = var.foundation_model
  instruction = var.instruction_writer
  agent_role_arn = module.agent_iam.agent_role_arn
  agent_role_name = "writer"  
  environment = "dev"
  tags = {
    Name = "my-ai-agent-writer"
    "${local.project_name}-env" = "${local.project_name}-resource"
  }
}

resource "aws_bedrockagent_agent_collaborator" "ax_lead_agent_search_collaborator" {
  agent_id                   = module.ai_agent_supervisor.agent_id
  collaboration_instruction  = var.instruction_web_search
  collaborator_name          = "my-collab-web-search"
  relay_conversation_history = "TO_COLLABORATOR"

  agent_descriptor {
    alias_arn = module.ai_agent_web_search.agent_alias_arn
  }

  timeouts {
    create = "3m"
  }
}

resource "aws_bedrockagent_agent_collaborator" "ax_lead_agent_writer_collaborator" {
  agent_id                   = module.ai_agent_supervisor.agent_id
  collaboration_instruction  = var.instruction_writer
  collaborator_name          = "my-collab-writer"
  relay_conversation_history = "TO_COLLABORATOR"

  agent_descriptor {
    alias_arn = module.ai_agent_writer.agent_alias_arn
  }

  timeouts {
    create = "3m"
  }

  depends_on = [ aws_bedrockagent_agent_collaborator.ax_lead_agent_search_collaborator ]
}

# module "lambda_action_group" {
#   source = "./modules/action_group"
#   project_name = local.project_name
  # lambda_function_arn = module.backend.lambda_function_arn
  # function_schema_name = "lambda_function_schema_name"
#   agent_id = module.ai_agent.agent_id
#   action_group_executor = {
#     lambda = module.backend.lambda_function_arn
#   }  
# }

# module "return_control_action_group" {
#   source = "./modules/action-group"
#   project_name = local.project_name  
#   function_schema_name = "save_file_schema"
#   agent_id = module.ai_agent.agent_id
#   action_group_executor =[{
#     lambda = "",
#     custom_control = "RETURN_CONTROL"
#   }]
# }

# resource "aws_ecr_repository" "ecr_repository" {
#   name = "${local.project_name}-ecr"
#   force_delete = true
#   tags = {
#     Name = "${local.project_name}-ecr"
#     "${local.project_name}-env" = "${local.project_name}-resource"
#   }
# }

# resource "aws_ecr_lifecycle_policy" "example" {
#   repository = aws_ecr_repository.ecr_repository.name
#   policy = jsonencode({
#     rules = [{
#       rulePriority = 1,
#       description  = "Keep last 30 images",
#       selection = {
#         tagStatus     = "any",
#         countType     = "imageCountMoreThan",
#         countNumber   = 30
#       },
#       action = {
#         type = "expire"
#       }
#     }]
#   })
# }

# data "aws_ecr_authorization_token" "token" {}

# # configure docker provider
# provider "docker" {
#   registry_auth {
#     address = "${aws_ecr_repository.ecr_repository.repository_url}"
#     username = data.aws_ecr_authorization_token.token.user_name
#     password = data.aws_ecr_authorization_token.token.password
#   }
# }

# # build backend image
# resource "docker_image" "backend_image" {
#   name = "${aws_ecr_repository.ecr_repository.repository_url}:latest"
#   build {
#     context = "${path.module}/../app/backend"
#   }
#   platform = "linux/arm64"
# }

# # push backend image to ECR
# resource "null_resource" "docker_push" {
#   provisioner "local-exec" {
#     command = <<EOT
#       docker build -t ${aws_ecr_repository.ecr_repository.repository_url}:latest .
#       aws ecr get-login-password --region ${data.aws_region.current.name} | docker login --username AWS --password-stdin ${aws_ecr_repository.ecr_repository.repository_url}
#       docker push ${aws_ecr_repository.ecr_repository.repository_url}:latest
#     EOT
#   }
# }

# # deploy backend service
# module "backend" {
#   source = "./modules/backend"
#   project_name = local.project_name
#   service_desired_count = 1 
#   vpc_id = module.vpc.vpc_id
#   container_image_uri = "${aws_ecr_repository.ecr_repository.repository_url}:latest"
#   environment_variables = [
#     {
#       name = "AWS_SQS_QUEUE_NAME"
#       value = module.sqs.queue_name
#     },    
#     {
#       name = "AWS_S3_BUCKET_NAME"
#       value = module.s3_bucket.bucket_name
#     },
#     {
#       name = "AWS_REGION"
#       value = data.aws_region.current.name
#     }
#   ]
#   public_subnets = module.vpc.public_subnets
#   tags = {
#     Name = "my-backend" 
#     "${local.project_name}-env" = "${local.project_name}-resource"
#   }
# }

resource "local_file" "env_file" {
  content = <<-EOT
    AWS_REGION="${data.aws_region.current.name}"
    AWS_S3_BUCKET_NAME="${module.s3_bucket.bucket_name}"
    AWS_BEDROCK_SUPERVISOR_AGENT_ID="${module.ai_agent_supervisor.agent_id}"
    AWS_BEDROCK_SUPERVISOR_AGENT_ALIAS_ID="${module.ai_agent_supervisor.agent_alias_id != null ? module.ai_agent_supervisor.agent_alias_id : ""}"
    AWS_BEDROCK_WEB_SEARCH_AGENT_ID="${module.ai_agent_web_search.agent_id}"
    AWS_BEDROCK_WEB_SEARCH_AGENT_ALIAS_ID="${module.ai_agent_web_search.agent_alias_id != null ? module.ai_agent_web_search.agent_alias_id : ""}"
    AWS_BEDROCK_WRITER_AGENT_ID="${module.ai_agent_writer.agent_id}"
    AWS_BEDROCK_WRITER_AGENT_ALIAS_ID="${module.ai_agent_writer.agent_alias_id != null ? module.ai_agent_writer.agent_alias_id : ""}"
  EOT
  filename = "${path.module}/.aws/.env"
}
