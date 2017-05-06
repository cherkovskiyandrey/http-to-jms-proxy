package ru.sbrf.domain.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HostAndPort;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import ratpack.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Value
public class HistoryData {

    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @JsonProperty("from")
    @JsonFormat(pattern = LOCAL_DATE_TIME_PATTERN)
    private LocalDateTime from;

    @JsonProperty("to")
    @JsonFormat(pattern = LOCAL_DATE_TIME_PATTERN)
    private LocalDateTime to;

    @JsonProperty("pageNumber")
    private int pageNumber;

    @JsonProperty("maxPage")
    private int maxPage;

    @JsonProperty("pageSize")
    private int pageSize;

    @Singular
    @JsonProperty("elements")
    private List<HistoryElement> historyElements;

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        String ss = objectMapper.writeValueAsString(
                HistoryData.builder()
                        .from(LocalDateTime.now())
                        .to(LocalDateTime.now())
                        .maxPage(100)
                        .pageSize(10000)
                        .pageNumber(1)
                        .historyElement(
                                HistoryElement.builder()
                                        .status(HistoryElement.Status.OK)
                                        .UUID("ddedwdwdwd")
                                        .bodySize(1)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .durationOfProxy(100)
                                        .receivedTimestamp(LocalDateTime.now())
                                        .sourceAddress(HostAndPort.fromHost("localhost").withDefaultPort(9999).toString())
                                        .build()
                        )
                        .build()
        );
        System.out.println(ss);

        String historyFilter = "{\"from\":\"2017-03-30T20:24\",\"to\":\"2017-03-30T20:46\",\"pageNumber\":10,\"pageSize\":100}";

        HistoryFilter filer = objectMapper.readValue(historyFilter, HistoryFilter.class);

        System.out.println(filer);
    }
}
