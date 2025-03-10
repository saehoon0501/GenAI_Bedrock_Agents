output "agent_role_arn" {
  value = aws_iam_role.agent_role.arn
}

output "agent_policy_arn" {
  value = aws_iam_policy.agent_policy.arn
}