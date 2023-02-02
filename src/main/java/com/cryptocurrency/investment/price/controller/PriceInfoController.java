package com.cryptocurrency.investment.price.controller;

import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.price.Service.PriceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/price")
@RestController
public class PriceInfoController {

    @Autowired
    private PriceInfoService priceInfoService;

    /**
     * TODO: Paging 으로 최적화 하기
     */
    @GetMapping("/{name}/1s")
    public ResponseWrapperDto priceInfoRedisMapping(@PathVariable String name) {
        try {
            return ResponseWrapperDto.of(ResponseStatus.PRICE_REQUEST_SUCCEED, priceInfoService.getPriceInfoRedis(name));
        } catch (Exception e) {
            return ResponseWrapperDto.of(ResponseStatus.PRICE_REQUEST_FAILED);
        }
    }

    @GetMapping("/{name}/{interval}/{unit}")
    public ResponseWrapperDto priceInfoMysqlMapping(@PathVariable String name,
                                                    @PathVariable Long interval,
                                                    @PathVariable String unit) {
        try {
            return ResponseWrapperDto.of(ResponseStatus.PRICE_REQUEST_SUCCEED, priceInfoService.getPriceInfoMysql(name, interval, unit));
        } catch (Exception e) {
            return ResponseWrapperDto.of(ResponseStatus.PRICE_REQUEST_FAILED);
        }
    }
}
