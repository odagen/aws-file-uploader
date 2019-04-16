resource "aws_iam_role" "time_fetcher_role" {
  name = "${local.lambda_role_name}"
  assume_role_policy = "${local.lambda_asume_role_policy}"
}

resource "aws_iam_role_policy" "time_write_policy" {
  policy = "${local.lambda_role_policy}"
  role = "${aws_iam_role.time_fetcher_role.id}"
}

resource "aws_lambda_function" "time_fetcher_lambda" {
  function_name = "${local.lambda_function_name}"
  handler = "${local.lambda_handler}"
  role = "${aws_iam_role.time_fetcher_role.arn}"
  runtime = "${local.lambda_runtime}"
  source_code_hash = "${filebase64sha256(local.lambda_file_name)}"
  filename = "${local.lambda_file_name}"
  memory_size = "${local.lambda_memory_size}"
  timeout = "${local.lambda_timeout}"
  environment {
    variables {
      env = "dev"
    }
  }
}

