package com.cryptocurrency.investment.price.dto.request;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class RequestPriceInfoDto {
    @JsonProperty("status")
    private String status;
    @JsonProperty("data")
    private InnerData innerData;
    /**
     * TODO : ConcurrentHashMap 으로 시간들이 뒤섞여서 저장되는것은 막았지만 아직 시간이 뛰엄뛰엄 저장됨 -> 메모리 문제인지 확인
     */
    private ConcurrentHashMap<String, RequestPriceInfoInnerDto> fields = new ConcurrentHashMap<>();
    @Data
    public class InnerData {
        @JsonProperty("date")
        private Long timestamp;
        @JsonAnySetter
        public void setField(String field, RequestPriceInfoInnerDto value) {
            fields.put(field, value);
        }
    }
}

