resource "aws_iam_role" "time_fetcher_role" {
  name = "time_fetcher_role"

  assume_role_policy = <<EOF
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
}

resource "aws_iam_role_policy" "time_write_policy" {
  policy = <<EOF
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
  role = "${aws_iam_role.time_fetcher_role.id}"
}

resource "aws_lambda_function" "time_fetcher_lambda" {
  function_name = "time_fetcher"
  handler = "com.epam.cdp.aws.lambda.TimeFetcher::handleRequest"
  role = "${aws_iam_role.time_fetcher_role.arn}"
  runtime = "java8"
  source_code_hash = "${filebase64sha256("../lambda/target/lambda-1.0-SNAPSHOT.jar")}"
  filename = "../lambda/target/lambda-1.0-SNAPSHOT.jar"
  memory_size = 256
  timeout = 30
}