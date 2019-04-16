package com.epam.cdp.aws.lambda;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.epam.cdp.aws.lambda.pojo.WorldClockResponse;
import com.epam.cdp.aws.lambda.utils.HttpFetcher;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.ZonedDateTime;

public class TimeFetcher implements RequestHandler<Object, WorldClockResponse> {

    private static final String API_URI = "http://worldclockapi.com/api/json/est/now";
    private static final String TIME_LOG_BUCKET = "time-log-odagen";

    private final AmazonS3 amazonS3;

    public TimeFetcher() {
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_CENTRAL_1)
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .build();
    }

    @Override
    public WorldClockResponse handleRequest(Object event, Context context) {
        String content = HttpFetcher.fetchContent(API_URI);
        WorldClockResponse response = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, zonedDateTimeDeserializer)
                .create()
                .fromJson(content, WorldClockResponse.class);
        context.getLogger().log("Got response: " + response);
        amazonS3.putObject(TIME_LOG_BUCKET, response.getCurrentFileTime().toString(), response.getCurrentDateTime().toString());

        return response;
    }

    private JsonDeserializer<ZonedDateTime> zonedDateTimeDeserializer = ((element, type, jsonContext) -> ZonedDateTime.parse(element.getAsString()));
}
