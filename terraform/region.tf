provider "aws" {
  region = "eu-central-1"
}

module "s3-static-site" {
  source = "s3-static-site"
}

module "s3-huge-objects" {
  source = "s3-huge-objects"
}
