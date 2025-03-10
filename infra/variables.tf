variable "foundation_model" {
  type = string
  description = "The foundation model to use for the agent"
}

variable "instruction_supervisor" {
  type = string
  description = "The instruction to use for the agent"
}

variable "instruction_web_search" {
  type = string
  description = "The instruction to use for the agent"
}

variable "instruction_writer" {
  type = string
  description = "The instruction to use for the agent"
}

variable "orchestration_prompt_configurations" {
  type = set(object({
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
  description = "The base prompt template to use for the agent"  
}

variable "environment" {
  type = string
  description = "The environment to use for the agent"
}
