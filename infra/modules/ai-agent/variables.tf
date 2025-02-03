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

variable "orchestration_prompt_template" {
    type = string
    description = "The base prompt template to use for the agent"
}

variable "orchestration_inference_configuration" {
    description = "Configuration for the orchestration inference settings"
    type = object({
        maximumLength = number
        stopSequences = list(string)
        temperature = number
        topP = number
        topK = number
    })
    default = {
        maximumLength = 2048
        stopSequences = []
        temperature = 0
        topP = 1
        topK = 250
    }
}

variable "tags" {
    type = map(string)
    description = "The tags to use for the agent"
}

variable "environment" {
    type = string
    description = "The environment to use for the agent"
}
