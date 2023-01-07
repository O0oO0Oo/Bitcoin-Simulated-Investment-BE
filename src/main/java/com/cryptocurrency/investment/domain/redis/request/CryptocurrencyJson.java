package com.cryptocurrency.investment.domain.redis.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@Data
public class CryptocurrencyJson {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private CryptocurrencyJsonInnerInfo cryptocurrencyJsonInnerInfo;

    private HashMap<String, CryptocurrencyJsonPriceData> fileds = new HashMap<>();

    public class CryptocurrencyJsonInnerInfo{

        @JsonProperty("date")
        private Long timestamp;

        @JsonAnySetter
        public void setFiled(String field, CryptocurrencyJsonPriceData value) {
            fileds.put(field, value);
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        @JsonAnyGetter
        public HashMap<String, com.cryptocurrency.investment.domain.redis.request.CryptocurrencyJsonPriceData> getFields() {
            return fileds;
        }

        public Long getTimestamp() {
            return timestamp;
        }
    }
}
