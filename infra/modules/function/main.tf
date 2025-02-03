data "aws_iam_policy_document" "lambda_assume_role_doc" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "lambda_execution_role" {
  name               = "example-lambda-execution-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_assume_role_doc.json
}

resource "aws_iam_role_policy_attachment" "lambda_basic_exec_attach" {
  role       = aws_iam_role.lambda_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSLambdaBasicExecutionRole"
}

# In a real scenario, you'll build and upload the Lambda zip to S3.
# This example just references a placeholder S3 object.
resource "aws_lambda_function" "example_lambda" {
  function_name = "example-lambda"
  s3_bucket     = aws_s3_bucket.lambda_code_bucket.bucket
  s3_key        = "path/to/lambda_code.zip"
  handler       = "index.handler"
  runtime       = "python3.9"
  role          = aws_iam_role.lambda_execution_role.arn
}