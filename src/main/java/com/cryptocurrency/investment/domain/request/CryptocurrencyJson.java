package com.cryptocurrency.investment.domain.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;

@Data
public class CryptocurrencyJson {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private CryptocurrencyJsonInnerInfo cryptocurrencyJsonInnerInfo;

    public class CryptocurrencyJsonInnerInfo{

        private HashMap<String, CryptocurrencyJsonPriceData> fileds = new HashMap<>();

        @JsonProperty("date")
        private Long timestamp;

        @JsonAnySetter
        public void setFiled(String field, CryptocurrencyJsonPriceData value) {
            fileds.put(field, value);
        }

        @JsonAnyGetter
        public HashMap<String, CryptocurrencyJsonPriceData> getFields() {
            return fileds;
        }

        public Long getTimestamp() {
            return timestamp;
        }
    }
}
