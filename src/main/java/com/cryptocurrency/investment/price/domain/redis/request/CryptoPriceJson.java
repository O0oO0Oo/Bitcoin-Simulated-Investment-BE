package com.cryptocurrency.investment.price.domain.redis.request;

import lombok.Data;


/**
 * only use @param closing_price
 * TODO: 사용하지 않는 데이터 저장 안하도록 개선
 */
@Data
public class CryptoPriceJson {
    private String opening_price;
    private String closing_price;
    private String min_price;
    private String max_price;
    private String units_traded;
    private String acc_trade_value;
    private String prev_closing_price;
    private String units_traded_24H;
    private String acc_trade_value_24H;
    private String fluctate_24H;
    private String fluctate_rate_24H;
}
