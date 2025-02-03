variable "project_name" {
  type        = string
  description = "The name of the project"
}

variable "service_desired_count" {
  type        = number
  description = "The desired count of the ECS service"
  default     = 1
}

variable "environment_variables" {
  type        = list(map(string))
  description = "The environment variables of the backend"
}

variable "tags" {
  type        = map(string)
  description = "The tags of the backend"
}

variable "container_image_uri" {
  type        = string
  description = "The container image URI of the backend"
}

variable "vpc_id" {
  type        = string
  description = "The VPC ID"
}

variable "public_subnets" {
  type        = list(string)
  description = "The public subnets"
}