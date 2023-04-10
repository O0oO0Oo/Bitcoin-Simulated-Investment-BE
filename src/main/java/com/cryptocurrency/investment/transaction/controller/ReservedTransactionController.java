package com.cryptocurrency.investment.transaction.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.transaction.dto.request.DeleteReservedTransactionRequestDto;
import com.cryptocurrency.investment.transaction.dto.request.ReservedTransactionRequestDto;
import com.cryptocurrency.investment.transaction.service.ReservedTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cryptos/tx/reserved")
@RequiredArgsConstructor
public class ReservedTransactionController {

    private final ReservedTransactionService reservedService;

    @GetMapping
    public @ResponseBody ResponseWrapperDto reservedList(Authentication authentication){
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_LIST_REQUEST_SUCCEED,
                reservedService.findReservedTx(authentication));
    }

    @PostMapping("/buy")
    public @ResponseBody ResponseWrapperDto reservedBuyAdd(@RequestBody @Valid
                                                        ReservedTransactionRequestDto reservedTransactionRequestDto,
                                                           Authentication authentication) throws Exception {
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_REQUEST_SUCCEED,
                reservedService.addReservedBuyTx(authentication, reservedTransactionRequestDto));
    }

    @PostMapping("/sell")
    public @ResponseBody ResponseWrapperDto reservedSellAdd(@RequestBody @Valid
                                                           ReservedTransactionRequestDto reservedTransactionRequestDto,
                                                           Authentication authentication) throws Exception {
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_REQUEST_SUCCEED,
                reservedService.addReservedSellTx(authentication, reservedTransactionRequestDto));
    }

    @DeleteMapping
    public @ResponseBody ResponseWrapperDto reservedRemove(@RequestBody @Valid
                                                               DeleteReservedTransactionRequestDto deleteReservedTransactionRequestDto,
                                                           Authentication authentication) {
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_REQUEST_SUCCEED,
                reservedService.deleteReservedTx(authentication, deleteReservedTransactionRequestDto));
    }
}
