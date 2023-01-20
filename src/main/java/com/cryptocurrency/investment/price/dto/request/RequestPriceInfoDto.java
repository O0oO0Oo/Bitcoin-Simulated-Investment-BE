package com.cryptocurrency.investment.price.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;

@Data
public class RequestPriceInfoDto {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private InnerData innerData;

    private HashMap<String, RequestPriceInfoInnerDto> fields = new HashMap<>();
    public class InnerData{
        @JsonProperty("date")
        private Long timestamp;
        @JsonAnySetter
        public void setFiled(String field, RequestPriceInfoInnerDto value) {
            fields.put(field, value);
        }

        public void setTimestamp(Long timestamp) {this.timestamp = timestamp;}
        public Long getTimestamp() {
            return timestamp;
        }
    }
}
