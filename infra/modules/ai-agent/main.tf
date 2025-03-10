

resource "aws_bedrockagent_agent" "agent" {
    agent_name = "${var.project_name}-agent-${var.agent_role_name}"
    agent_resource_role_arn = var.agent_role_arn
    idle_session_ttl_in_seconds = 3600    
    foundation_model = var.foundation_model 
    instruction = var.instruction
    prepare_agent = var.prepare_agent
    dynamic "prompt_override_configuration" {
        for_each = var.custom_prompt_configuration == null? {} : var.custom_prompt_configuration
        content {
          dynamic "prompt_configurations" {
            for_each = prompt_override_configuration.value.prompt_configurations
            content {
              base_prompt_template = prompt_configurations.value.base_prompt_template          
              parser_mode = prompt_configurations.value.parser_mode
              prompt_creation_mode = prompt_configurations.value.prompt_creation_mode
              prompt_state = prompt_configurations.value.prompt_state
              prompt_type = prompt_configurations.value.prompt_type                            
              inference_configuration = [{
                max_length = prompt_configurations.value.inference_configuration.maximumLength
                stop_sequences = prompt_configurations.value.inference_configuration.stopSequences
                temperature = prompt_configurations.value.inference_configuration.temperature
                top_p = prompt_configurations.value.inference_configuration.topP
                top_k = prompt_configurations.value.inference_configuration.topK
              }]
            }
          }
        }
    }
    agent_collaboration = var.agent_collaboration
    tags = var.tags
}

resource "aws_bedrockagent_agent_alias" "agent_alias" {
  count = var.prepare_agent ? 1 : 0
  agent_alias_name = "${var.project_name}-agent-${var.environment}-${var.agent_role_name}"
  agent_id = aws_bedrockagent_agent.agent.id
  description = "Alias for the ${var.environment} agent"
}

