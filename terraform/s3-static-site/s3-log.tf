resource "aws_s3_bucket" "s3-log" {
  bucket = "static-site-log-odagen"
  acl = "log-delivery-write"
}