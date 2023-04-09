package com.cryptocurrency.investment.transaction.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.transaction.dto.request.DeleteReservedTransactionDto;
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
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_REQUEST_SUCCEED,
                reservedService.findReservedTx(authentication));
    }

    @PostMapping
    public @ResponseBody ResponseWrapperDto reservedAdd(@RequestBody @Valid
                                                        ReservedTransactionRequestDto reservedTransactionRequestDto,
                                                        Authentication authentication) throws Exception {
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_REQUEST_SUCCEED,
                reservedService.addReservedTx(authentication, reservedTransactionRequestDto));
    }

    @DeleteMapping
    public @ResponseBody ResponseWrapperDto reservedRemove(@RequestBody @Valid
                                                           DeleteReservedTransactionDto deleteReservedTransactionDto,
                                                           Authentication authentication) {
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_REQUEST_SUCCEED,
                reservedService.deleteReservedTx(authentication, deleteReservedTransactionDto));
    }
}
