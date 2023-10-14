package com.cryptocurrency.investment.price.dto.scheduler;

import lombok.Data;

import java.util.Comparator;

@Data
public class PriceInfoDto {
    String name;
    String price;
    Long timestamp;

    public PriceInfoDto(String name, String price, Long timestamp) {
        this.name = name;
        this.price = price;
        this.timestamp = timestamp;
    }

    public static Comparator<PriceInfoDto> timestampComparator = new Comparator<PriceInfoDto>() {
        @Override
        public int compare(PriceInfoDto o1, PriceInfoDto o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    };
}
