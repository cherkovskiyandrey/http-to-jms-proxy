package ru.sbrf.domain.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AggregationStat {
    @JsonProperty("success")
    private final long successAmount;

    @JsonProperty("process")
    private final long inProgressAmount;

    @JsonProperty("error")
    private final long errorAmount;

    public AggregationStat(long successAmount, long inProgressAmount, long errorAmount) {
        this.successAmount = successAmount;
        this.inProgressAmount = inProgressAmount;
        this.errorAmount = errorAmount;
    }

    public long getSuccessAmount() {
        return successAmount;
    }

    public long getInProgressAmount() {
        return inProgressAmount;
    }

    public long getErrorAmount() {
        return errorAmount;
    }

    @Override
    public String toString() {
        return "ServerStatus{" +
                "successAmount=" + successAmount +
                ", inProgressAmount=" + inProgressAmount +
                ", errorAmount=" + errorAmount +
                '}';
    }
}
