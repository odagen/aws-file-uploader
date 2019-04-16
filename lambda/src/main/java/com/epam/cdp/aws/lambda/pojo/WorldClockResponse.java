package com.epam.cdp.aws.lambda.pojo;


import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class WorldClockResponse {

    @SerializedName("$id")
    private String id;
    @SerializedName("currentDateTime")
    private ZonedDateTime currentDateTime;
    @SerializedName("utcOffset")
    private String utcOffset;
    @SerializedName("isDayLightSavingsTime")
    private Boolean isDayLightSavingsTime;
    @SerializedName("dayOfTheWeek")
    private String dayOfTheWeek;
    @SerializedName("timeZoneName")
    private String timeZoneName;
    @SerializedName("currentFileTime")
    private Long currentFileTime;
    @SerializedName("ordinalDate")
    private String ordinalDate;

    public ZonedDateTime getCurrentDateTime() {
        return currentDateTime;
    }

    public Long getCurrentFileTime() {
        return currentFileTime;
    }

    @Override
    public String toString() {
        return "WorldClockResponse{" +
               "id='" + id + '\'' +
               ", currentDateTime='" + currentDateTime + '\'' +
               ", utcOffset='" + utcOffset + '\'' +
               ", isDayLightSavingsTime=" + isDayLightSavingsTime +
               ", dayOfTheWeek='" + dayOfTheWeek + '\'' +
               ", timeZoneName='" + timeZoneName + '\'' +
               ", currentFileTime=" + currentFileTime +
               ", ordinalDate='" + ordinalDate + '\'' +
               '}';
    }
}
