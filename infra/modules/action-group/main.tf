resource "aws_bedrockagent_agent_action_group" "agent_action_group" {
  action_group_name          = "${var.project_name}-${var.function_schema_name}-action"
  agent_id                   = var.agent_id
  agent_version              = "DRAFT"
  skip_resource_in_use_check = true 
  dynamic "action_group_executor" {
    for_each = var.action_group_executor[*]
    content {
      lambda = action_group_executor.value.lambda != "" ? action_group_executor.value.lambda : null
      custom_control = action_group_executor.value.custom_control != "" ? action_group_executor.value.custom_control : null
    }
  }
  api_schema {
    payload = file("${path.module}/schema/${var.function_schema_name}.yaml")
  }
}