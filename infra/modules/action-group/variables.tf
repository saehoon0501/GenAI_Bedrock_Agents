variable "project_name" {
  type = string
}

variable "function_schema_name" {
  type = string
}

variable "agent_id" {
  type = string
}

variable "action_group_executor" {
  type = list(object({
    lambda = string
    custom_control = string
  }))
  default = [{
    lambda = ""
    custom_control = ""
  }]
}