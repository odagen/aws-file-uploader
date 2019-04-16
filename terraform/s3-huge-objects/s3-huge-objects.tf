resource "aws_s3_bucket" "s3-huge-objects" {
  bucket = "huge-objects-odagen"
  acl = "private"

  tags {
    Name = "odagen-huge-bucket"
    Environment = "dev"
  }

  versioning {
    enabled = "true"
  }

  lifecycle_rule {
    enabled = false

    transition {
      days = 30
      storage_class = "STANDARD_IA"
    }

    transition {
      days = 60
      storage_class = "GLACIER"
    }

    expiration {
      days = 90
    }
  }
}