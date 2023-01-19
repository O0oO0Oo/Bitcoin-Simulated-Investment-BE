package com.cryptocurrency.investment.price.domain.redis.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;

@Data
public class CryptoJson {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private CryptoJsonInner cryptocurrencyJsonInnerInfo;

    private HashMap<String, CryptoPriceJson> fields = new HashMap<>();
    public class CryptoJsonInner{
        @JsonProperty("date")
        private Long timestamp;
        @JsonAnySetter
        public void setFiled(String field, CryptoPriceJson value) {
            fields.put(field, value);
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
        @JsonAnyGetter
        public HashMap<String, CryptoPriceJson> getFields() {
            return fields;
        }
        public Long getTimestamp() {
            return timestamp;
        }
    }
}
