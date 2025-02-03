resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-ecs-cluster"
}

# ECS Tasks Trust Policy
data "aws_iam_policy_document" "ecs_tasks_trust" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

# IAM Role for ECS Tasks
resource "aws_iam_role" "bedrock_ecs_task_role" {
  name               = "BedrockEcsTaskRole"
  assume_role_policy = data.aws_iam_policy_document.ecs_tasks_trust.json
  description        = "ECS Task Role to allow calling Amazon Bedrock."
}

# Bedrock Policy Document
data "aws_iam_policy_document" "bedrock_ecs_task_policy" {
  statement {
    sid       = "BedrockPermissions"
    actions   = [
      "bedrock:InvokeModel",
      "bedrock:InvokeModelWithResponseStream",
      "bedrock:ListFoundationModels",
      "bedrock:DescribeModel",
      "bedrock:ListCustomModels",
      "bedrock:DescribeCustomModel"
    ]
    resources = ["*"]
  }
}

# IAM Policy for Bedrock
resource "aws_iam_policy" "bedrock_ecs_task_policy" {
  name        = "BedrockEcsTaskPolicy"
  description = "Policy granting ECS tasks permission to call Amazon Bedrock."
  policy      = data.aws_iam_policy_document.bedrock_ecs_task_policy.json
}

# Attach Policy to Task Role
resource "aws_iam_role_policy_attachment" "bedrock_ecs_task_policy_attach" {
  role       = aws_iam_role.bedrock_ecs_task_role.name
  policy_arn = aws_iam_policy.bedrock_ecs_task_policy.arn
}

# IAM Role for ECS Execution
resource "aws_iam_role" "ecs_execution_role" {
  name               = "EcsExecutionRole"
  assume_role_policy = data.aws_iam_policy_document.ecs_tasks_trust.json
  description        = "ECS Execution Role to pull images from ECR and push logs to CloudWatch."
}

# ECS Execution Role - Policy Doc
data "aws_iam_policy_document" "ecs_execution_policy" {
  statement {
    sid    = "AllowPullECRImages"
    effect = "Allow"
    actions = [
      "ecr:GetAuthorizationToken",
      "ecr:BatchCheckLayerAvailability",
      "ecr:GetDownloadUrlForLayer",
      "ecr:BatchGetImage"
    ]
    resources = ["*"]
  }

  statement {
    sid    = "AllowCloudWatchLogs"
    effect = "Allow"
    actions = [
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = ["arn:aws:logs:*:*:*"]
  }
}

# ECS Execution Role - Policy
resource "aws_iam_policy" "ecs_execution_policy" {
  name        = "EcsExecutionPolicy"
  description = "Allows ECS to pull images from ECR and send logs to CloudWatch."
  policy      = data.aws_iam_policy_document.ecs_execution_policy.json
}

# Attach Policy to Execution Role
resource "aws_iam_role_policy_attachment" "ecs_execution_policy_attach" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = aws_iam_policy.ecs_execution_policy.arn
}

# Security Group for ECS tasks
resource "aws_security_group" "ecs_sg" {
  name        = "${aws_ecs_cluster.main.name}-sg"
  description = "Security group for ECS tasks"
  vpc_id      = var.vpc_id

  ingress {
    description      = "Allow all inbound for demo"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  egress {
    description      = "Allow all outbound"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }
}

resource "aws_ecs_task_definition" "backend" {
  family                   = "${aws_ecs_cluster.main.name}-backend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 256
  memory                   = 512
  task_role_arn            = aws_iam_role.bedrock_ecs_task_role.arn
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  container_definitions    = jsonencode([
    {
      name = "backend"
      image = "${var.container_image_uri}"
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort = 8080
        }
      ]
      environment = var.environment_variables
    }
  ])
}

resource "aws_ecs_service" "backend_service" {
  name            = "${aws_ecs_cluster.main.name}-backend-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.backend.arn
  desired_count   = var.service_desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.public_subnets
    security_groups  = [aws_security_group.ecs_sg.id]
    assign_public_ip = true
  }

  # Ensure task definition is created before ECS service
  depends_on = [
    aws_ecs_task_definition.backend
  ]

  tags = var.tags
}