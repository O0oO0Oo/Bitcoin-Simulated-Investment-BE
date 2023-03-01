package com.cryptocurrency.investment.wallet.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.cryptocurrency.investment.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping
    public @ResponseBody ResponseWrapperDto walletDetails(Authentication authentication) {
        UUID id = UUID.fromString(authentication.getName());
        List<Wallet> wallet = walletService.findWallet(id);
        if (wallet.isEmpty()) {
            return ResponseWrapperDto.of(ResponseStatus.WALLET_REQUEST_FAILED_EMPTY);
        }

        return ResponseWrapperDto.of(ResponseStatus.WALLET_REQUEST_SUCCEED, wallet);
    }

    /**
     * TODO: 유저들 지갑 리스트 나중
     * @return
     */
    @GetMapping("/list")
    public @ResponseBody ResponseWrapperDto walletList(){
        return ResponseWrapperDto.of(ResponseStatus.INVALID_PERMISSION);
    }
}