package com.cryptocurrency.investment.transaction.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.service.CryptoService;
import com.cryptocurrency.investment.price.dto.scheduler.PricePerMinuteDto;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.transaction.domain.TransactionType;
import com.cryptocurrency.investment.transaction.dto.request.TransactionRequestDto;
import com.cryptocurrency.investment.transaction.dto.response.TransactionResponseDto;
import com.cryptocurrency.investment.transaction.service.TransactionService;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.service.UserService;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.cryptocurrency.investment.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/cryptos/tx")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final CryptoService cryptoService;
    private final UserService userService;
    private final WalletService walletService;
    private final PricePerMinuteDto pricePerMinuteDto;

    @GetMapping
    public @ResponseBody ResponseWrapperDto txList(Authentication authentication){
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_LIST_REQUEST_SUCCEED,
                transactionService.findTx(authentication));
    }

    @GetMapping("/{name}")
    public @ResponseBody ResponseWrapperDto txNameList(@PathVariable("name") String name,
                                                                Authentication authentication) {
        return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_LIST_REQUEST_SUCCEED,
                transactionService.findNameTx(authentication, name));
    }

    @PostMapping
    public @ResponseBody ResponseWrapperDto txNameAdd(@RequestBody TransactionRequestDto txDto,
                                                      Authentication authentication,
                                                      BindingResult bindingResult) {
        // 현재가에 팔기가 아닐때
        if (txDto.type().equals(TransactionType.RESERVE_SELL) || txDto.type().equals(TransactionType.RESERVE_BUY)) {
            return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_SELL_FAILED_INVALID_TYPE);
        }

        Optional<UserAccount> userOpt = userService.findUserById(authentication);

        // 유저 정보 불러오기 실패
        if (userOpt.isEmpty()) {
            return ResponseWrapperDto.of(ResponseStatus.USER_INFO_GET_FAILED);
        }
        UserAccount user = userOpt.get();

        Optional<Wallet> walletOpt = walletService.findWallet(user.getId(), txDto.name());
        // 지갑 없음 && SELL 기능
        if (walletOpt.isEmpty() && txDto.type().equals(TransactionType.SELL)) {
            return ResponseWrapperDto.of(ResponseStatus.WALLET_REQUEST_FAILED);
        }
        Wallet wallet;
        // 지갑 초기화
        if (walletOpt.isPresent()) {
            wallet = walletOpt.get();
        }
        else{
            wallet = new Wallet();
            wallet.setUserAccount(user);
        }

        // 보유 코인 이상으로 팔떄
        if (txDto.type().equals(TransactionType.SELL) && (wallet.getAmount() < txDto.amount())) {
            return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_SELL_FAILED_AMOUNT_LACKED);
        }

        Optional<Crypto> cryptoOpt = cryptoService.userFindStatusCrypto(txDto);
        // 거래 가능한 상태의 코인 아님
        if (cryptoOpt.isEmpty()) {
            return ResponseWrapperDto.of(ResponseStatus.TRANSACTION_REQUEST_FAILED_INVALID_CRYPTO);
        }
        Crypto crypto = cryptoOpt.get();
        wallet.setCrypto(crypto);
        wallet.setName(crypto.getName());

        // 현재 가격
        Double price = Double.parseDouble(pricePerMinuteDto.getPriceHashMap().get(crypto.getName()).getCurPrice());

        // 돈 부족
        if (txDto.type().equals(TransactionType.BUY) &&
                (user.getMoney() < txDto.amount() * price)
        ) {
            return ResponseWrapperDto.of(
                    ResponseStatus.TRANSACTION_BUY_REQUEST_FAILED_MONEY_LAKED
            );
        }

        // 1000원 단위 이하 주문시 취소
        if (price * txDto.amount() < 1000.0) {
            return ResponseWrapperDto.of(
                    ResponseStatus.TRANSACTION_REQUEST_FAILED_1000
            );
        }

        Transaction tx = transactionService.addTx(
                crypto,
                user,
                price,
                txDto
        );

        walletService.addWallet(wallet, user, price, txDto);

        return ResponseWrapperDto.of(
                ResponseStatus.TRANSACTION_REQUEST_SUCCEED, TransactionResponseDto.of(tx, user)
        );
    }
}
