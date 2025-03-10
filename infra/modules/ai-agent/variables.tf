variable "s3_bucket_arn" {
    type = string
    description = "The ARN of the S3 bucket"
    default = "*"
}

variable "lambda_function_arn" {
    type = string
    description = "The ARN of the lambda function"
    default = "*"
}


variable "project_name" {
    type = string
    description = "The name of the project"
}

variable "foundation_model" {
    type = string
    description = "The foundation model to use for the agent"
}

variable "instruction" {
    type = string
    description = "The instruction to use for the agent"
}

variable "custom_prompt_configuration" {
    description = "The custom prompt configuration to use for the agent"
    type = map(object({
        prompt_configurations = set(object({
            base_prompt_template = string
            parser_mode = string
            prompt_creation_mode = string
            prompt_state = string
            prompt_type = string
            inference_configuration = object({
                maximumLength = number
                stopSequences = list(string)
                temperature = number
                topP = number
                topK = number
            })
        }))        
    }))
    default = null
}

variable "agent_role_arn" {
    type = string
    description = "The ARN of the agent role"
}

variable "tags" {
    type = map(string)
    description = "The tags to use for the agent"
}

variable "environment" {
    type = string
    description = "The environment to use for the agent"
}

variable "agent_collaboration" {
    type = string
    description = "The agent collaboration to use for the agent"
    default = "DISABLED"
}

variable "agent_role_name" {
    type = string
    description = "The name of the agent role"
}

variable "prepare_agent" {
    type = bool
    description = "Whether to prepare the agent"
    default = true
}