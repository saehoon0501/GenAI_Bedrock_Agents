resource "aws_s3_bucket" "content-bucket" {
  bucket = "${var.project_name}-content-bucket-${random_id.bucket_suffix.hex}"  
  tags = var.tags
}

# Just a random suffix to avoid bucket name collisions
resource "random_id" "bucket_suffix" {
  byte_length = 2
}

resource "aws_s3_bucket_ownership_controls" "bucket_ownership_controls" {
  bucket = aws_s3_bucket.content-bucket.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "bucket_public_access_block" {
  bucket = aws_s3_bucket.content-bucket.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_acl" "bucket_acl" {
  depends_on = [
    aws_s3_bucket_ownership_controls.bucket_ownership_controls,
    aws_s3_bucket_public_access_block.bucket_public_access_block,
  ]

  bucket = aws_s3_bucket.content-bucket.id
  acl    = "public-read"
}
