resource "aws_s3_bucket" "time-log-odagen" {
  bucket = "${local.bucket_name}"
  acl = "${local.bucket_acl}"
}