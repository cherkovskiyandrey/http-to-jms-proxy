package ru.sbrf.domain.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.sbrf.domain.statistics.AggregationStat;


public class ServerStatus {

    @JsonProperty("status")
    private final AggregationStat aggregationStat;

    @JsonProperty("curInput")
    private final long activeInputAmountRequests;

    @JsonProperty("curOutput")
    private final long activeOutputAmountRequests;

    public ServerStatus(AggregationStat aggregationStat, long activeInputAmountRequests, long activeOutputAmountRequests) {
        this.aggregationStat = aggregationStat;
        this.activeInputAmountRequests = activeInputAmountRequests;
        this.activeOutputAmountRequests = activeOutputAmountRequests;
    }

    @Override
    public String toString() {
        return "ServerStatus{" +
                "aggregationStat=" + aggregationStat +
                ", activeInputAmountRequests=" + activeInputAmountRequests +
                ", activeOutputAmountRequests=" + activeOutputAmountRequests +
                '}';
    }
}
