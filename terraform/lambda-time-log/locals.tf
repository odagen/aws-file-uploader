locals {
  lambda_asume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": [
          "lambda.amazonaws.com"
        ]
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

  lambda_role_policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "",
            "Effect": "Allow",
            "Action": "s3:PutObject",
            "Resource": "arn:aws:s3:::${aws_s3_bucket.time-log-odagen.id}/*"
        }
    ]
}
EOF

  lambda_role_name = "time_fetcher_role"
  lambda_function_name = "time_fetcher"
  lambda_file_name = "../lambda/target/lambda-1.0-SNAPSHOT.jar"
  lambda_handler = "com.epam.cdp.aws.lambda.TimeFetcher::handleRequest"
  lambda_runtime = "java8"
  lambda_memory_size = 256
  lambda_timeout = 30
}

locals {
  bucket_name = "time-log-odagen"
  bucket_acl = "private"
}