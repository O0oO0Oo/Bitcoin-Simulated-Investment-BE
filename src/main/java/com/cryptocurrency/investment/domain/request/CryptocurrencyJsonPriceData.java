package com.cryptocurrency.investment.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * only use @param closing_price
 * TODO: 사용하지 않는 데이터 저장 안하도록 개선
 */
@Data
public class CryptocurrencyJsonPriceData {
    private Double opening_price;
    private Double closing_price;
    private Double min_price;
    private Double max_price;
    private Double units_traded;
    private Double acc_trade_value;
    private Double prev_closing_price;
    private Double units_traded_24H;
    private Double acc_trade_value_24H;
    private Double fluctate_24H;
    private Double fluctate_rate_24H;
}
