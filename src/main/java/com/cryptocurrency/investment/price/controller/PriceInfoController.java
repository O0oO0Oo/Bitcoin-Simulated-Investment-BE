package com.cryptocurrency.investment.price.controller;

import com.cryptocurrency.investment.price.Service.PriceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/crypto")
@RestController
public class PriceInfoController {

    @Autowired
    private PriceInfoService priceInfoService;

    /**
     * TODO: Paging 으로 최적화 하기
     */
    @GetMapping("/{name}/1s")
    public ResponseEntity<?> cryptoPriceInfo(@PathVariable String name) {
        return ResponseEntity.ok(priceInfoService.getPriceInfo(name));
    }
}
