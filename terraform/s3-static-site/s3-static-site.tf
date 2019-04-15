resource "aws_s3_bucket" "s3-static-site" {
  bucket = "static-site-odagen"
  acl = "public-read"

  website {
    index_document = "index.html"
    error_document = "error.html"
  }

  logging {
    target_bucket = "${aws_s3_bucket.s3-log.id}"
    target_prefix = "static-site/"
  }
}