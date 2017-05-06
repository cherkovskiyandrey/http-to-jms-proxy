package ru.cherkovskiy.domain.history;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
@JsonClassDescription("element")
public class HistoryElement {

    public enum Status {

        @JsonProperty("ok")
        OK,

        @JsonProperty("fail")
        FAIL;
    }

    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    @JsonProperty("receivedTimestamp")
    @JsonFormat(pattern = LOCAL_DATE_TIME_PATTERN)
    private LocalDateTime receivedTimestamp;

    @JsonProperty("uuid")
    private String UUID;

    @JsonProperty("durationOfProxy")
    private long durationOfProxy;

    @JsonProperty("sourceAddress")
    private String sourceAddress;

    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("bodySize")
    private long bodySize;

    @JsonProperty("status")
    private Status status;
}
