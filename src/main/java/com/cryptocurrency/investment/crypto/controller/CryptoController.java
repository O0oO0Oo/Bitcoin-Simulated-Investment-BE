package com.cryptocurrency.investment.crypto.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import com.cryptocurrency.investment.crypto.dto.CryptoDto;
import com.cryptocurrency.investment.crypto.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cryptos")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @GetMapping("/list")
    public @ResponseBody ResponseWrapperDto cryptoList() {
        return ResponseWrapperDto.of(ResponseStatus.CRYPTO_REQUEST_SUCCEED,
                cryptoService.userFindCrypto().stream()
                        .map(
                                crypto -> CryptoDto.of(crypto)
                        )
        );
    }

    @GetMapping("/{status}/list")
    public @ResponseBody ResponseWrapperDto cryptoStatusList(@PathVariable("status") String status) {
        if (status.equals("NOT_USER")) {
            return ResponseWrapperDto.of(status, ResponseStatus.CRYPTO_INVALID_STATUS);
        }

        try{
            return ResponseWrapperDto.of(ResponseStatus.CRYPTO_REQUEST_SUCCEED,
                    cryptoService.userFindStatusCrypto(
                                    CryptoStatus.valueOf(status) // IllegalArgumentException
                            )
                            .stream()
                            .map(
                                    crypto -> CryptoDto.of(crypto)
                            )
            );
        }
        catch (IllegalArgumentException e){
            return ResponseWrapperDto.of(status, ResponseStatus.CRYPTO_INVALID_STATUS);
        }
    }
}
