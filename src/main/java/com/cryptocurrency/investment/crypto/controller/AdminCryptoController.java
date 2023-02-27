package com.cryptocurrency.investment.crypto.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import com.cryptocurrency.investment.crypto.dto.CryptoDto;
import com.cryptocurrency.investment.crypto.dto.CryptoModifyDto;
import com.cryptocurrency.investment.crypto.service.CryptoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/crypto")
@RequiredArgsConstructor
public class AdminCryptoController {

    private final CryptoService cryptoService;

    /**
     * 조회
     */
    @GetMapping
    public @ResponseBody ResponseWrapperDto cryptoList(){
        return ResponseWrapperDto.of(ResponseStatus.CRYPTO_REQUEST_SUCCEED, cryptoService.adminFindCrypto());
    }

    /**
     * 등록
     */
    @PostMapping
    public @ResponseBody ResponseWrapperDto cryptoAdd(@RequestBody @Valid CryptoDto cryptoDto,
                                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        if (cryptoService.adminFindCrypto(cryptoDto)){
            return ResponseWrapperDto.of(cryptoDto.name().toUpperCase(),ResponseStatus.CRYPTO_NAME_EXIST);
        }

        return ResponseWrapperDto.of(ResponseStatus.CRYPTO_REQUEST_SUCCEED,
                CryptoDto.of(
                        cryptoService.addCrypto(cryptoDto)
                )
        );
    }

    /**
     * TODO : 수정하는 정보가 똑같을시
     * 수정
     */
    @PutMapping
    public @ResponseBody ResponseWrapperDto cryptoModify(@RequestBody @Valid CryptoModifyDto modifyDto,
                                                         BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        if (!cryptoService.adminFindCrypto(modifyDto.name())){
            return ResponseWrapperDto.of(modifyDto.name().toUpperCase(),ResponseStatus.CRYPTO_NAME_NOT_EXIST);
        }

        if (!modifyDto.name().equals(modifyDto.newName()) && cryptoService.adminFindCrypto(modifyDto.newName())){
            return ResponseWrapperDto.of(modifyDto.newName().toUpperCase(),ResponseStatus.CRYPTO_NAME_EXIST);
        }

        if (cryptoService.modifyCrypto(modifyDto) == 1) {
            return ResponseWrapperDto.of(ResponseStatus.CRYPTO_PUT_SUCCEED);
        }
        else{
            return ResponseWrapperDto.of(ResponseStatus.CRYPTO_PUT_FAILED);
        }
    }

    /**
     * TODO: 유저가 보유한 코인일시
     */
    @DeleteMapping
    public @ResponseBody ResponseWrapperDto cryptoRemove(@RequestBody @Valid CryptoDto cryptoDto,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseWrapperDto.of(fieldError(bindingResult), ResponseStatus.INVALID_FORMAT);
        }

        if (!cryptoService.adminFindCrypto(cryptoDto)){
            return ResponseWrapperDto.of(cryptoDto.name().toUpperCase(),ResponseStatus.CRYPTO_NAME_NOT_EXIST);
        }

        if (cryptoService.removeCrypto(cryptoDto) == 1) {
            return ResponseWrapperDto.of(ResponseStatus.CRYPTO_DELETE_SUCCEED);
        }
        else{
            return ResponseWrapperDto.of(ResponseStatus.CRYPTO_DELETE_FAILED);
        }
    }

    public String fieldError(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream().map(
                FieldError::getField
        ).collect(Collectors.joining(", "));
    }
}
