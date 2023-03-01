package com.cryptocurrency.investment.transaction.controller;

import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.transaction.service.ReservedTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cryptos/tx/reserved")
@RequiredArgsConstructor
public class ReservedTransactionController {

    private final ReservedTransactionService reservedService;

//    @GetMapping
//    public @ResponseBody ResponseWrapperDto reservedList(Authentication authentication){
//
//    }
//
//    @PostMapping
//    public @ResponseBody ResponseWrapperDto reservedAdd(Authentication authentication) {
//
//    }
//
//    @PutMapping
//    public @ResponseBody ResponseWrapperDto reservedModify(Authentication authentication){
//
//    }
//
//    @DeleteMapping
//    public @ResponseBody ResponseWrapperDto reservedRemove(Authentication authentication){
//
//    }
}
