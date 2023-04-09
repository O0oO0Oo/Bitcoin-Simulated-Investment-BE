package com.cryptocurrency.investment.price.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.price.Service.PriceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cryptos/price")
@RequiredArgsConstructor
public class PriceController {

    private final PriceInfoService priceInfoService;

    /**
     * TODO: Paging 으로 최적화 하기
     */
    @GetMapping("/{name}/{interval}/s")
    public ResponseWrapperDto redisPriceList(@PathVariable String name,
                                             @PathVariable Long interval) {
        try {
            return ResponseWrapperDto.of(ResponseStatus.PRICE_REQUEST_SUCCEED,
                    priceInfoService.redisFindPrice(name, interval));
        } catch (Exception e) {
            return ResponseWrapperDto.of(ResponseStatus.PRICE_REQUEST_FAILED);
        }
    }

    /**
     * TODO 분단위 결과 개선
     */
    @GetMapping("/{name}/{interval}/{unit}")
    public ResponseWrapperDto mysqlPriceList(@PathVariable String name,
                                                    @PathVariable Long interval,
                                                    @PathVariable String unit) {
        try {
            return ResponseWrapperDto.of(ResponseStatus.PRICE_REQUEST_SUCCEED, priceInfoService.mysqlFindPrice(name, interval, unit));
        } catch (Exception e) {
            return ResponseWrapperDto.of(ResponseStatus.PRICE_REQUEST_FAILED);
        }
    }
}
