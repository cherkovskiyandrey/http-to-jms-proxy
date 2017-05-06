package ru.sbrf.domain.history;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class HistoryFilter {

    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final LocalDateTime from;
    private final LocalDateTime to;
    private final int pageNumber;
    private final int pageSize;

    @JsonCreator
    public HistoryFilter(@JsonProperty("from") @JsonFormat(pattern = LOCAL_DATE_TIME_PATTERN) LocalDateTime from,
                         @JsonProperty("to") @JsonFormat(pattern = LOCAL_DATE_TIME_PATTERN) LocalDateTime to,
                         @JsonProperty("pageNumber") int pageNumber,
                         @JsonProperty("pageSize") int pageSize) {
        this.from = from;
        this.to = to;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
