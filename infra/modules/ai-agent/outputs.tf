output "agent_id" {
  value = aws_bedrockagent_agent.agent.agent_id
}

output "agent_alias_id" {
  value = length(aws_bedrockagent_agent_alias.agent_alias) > 0 ? aws_bedrockagent_agent_alias.agent_alias[0].agent_alias_id : null
  description = "ID of the first agent alias (if any exist)"
}

output "agent_alias_arn" {
  value = length(aws_bedrockagent_agent_alias.agent_alias) > 0 ? aws_bedrockagent_agent_alias.agent_alias[0].agent_alias_arn : null
  description = "ARN of the first agent alias (if any exist)"
}