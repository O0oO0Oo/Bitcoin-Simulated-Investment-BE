package com.cryptocurrency.investment.price.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({
        "opening_price",
        "min_price",
        "max_price",
        "units_traded",
        "acc_trade_value",
        "prev_closing_price",
        "units_traded_24H",
        "acc_trade_value_24H",
        "fluctate_24H",
        "fluctate_rate_24H"})
public class RequestPriceInfoInnerDto {
    private String closing_price;
}