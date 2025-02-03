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
  type = object({
    maximumLength = number
    stopSequences = list(string)
    temperature = number
    topP = number
    topK = number
  })
  description = "The inference configuration to use for the agent"
}

variable "environment" {
  type = string
  description = "The environment to use for the agent"
}