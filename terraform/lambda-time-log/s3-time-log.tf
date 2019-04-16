resource "aws_s3_bucket" "time-log-odagen" {
  bucket = "time-log-odagen"
  acl = "private"
}