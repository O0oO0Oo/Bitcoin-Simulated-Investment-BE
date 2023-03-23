package com.cryptocurrency.investment.crypto.controller;

import com.cryptocurrency.investment.config.response.ResponseStatus;
import com.cryptocurrency.investment.config.response.ResponseWrapperDto;
import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.dto.request.FavoriteRequestDto;
import com.cryptocurrency.investment.crypto.service.CryptoService;
import com.cryptocurrency.investment.crypto.service.FavoriteCryptoService;
import com.cryptocurrency.investment.user.domain.UserAccount;
import com.cryptocurrency.investment.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cryptos/favorite")
@RequiredArgsConstructor
public class FavoriteCryptoController {
    private final FavoriteCryptoService favoriteCryptoService;
    private final CryptoService cryptoService;
    private final UserService userService;

    @GetMapping
    public @ResponseBody ResponseWrapperDto favoriteCryptoList(Authentication authentication) {
        return ResponseWrapperDto.of(ResponseStatus.FAVORITE_CRYPTO_SUCCEED,
                favoriteCryptoService.findFavoriteCrypto(authentication));
    }

    @GetMapping("/{status}")
    public @ResponseBody ResponseWrapperDto favoriteCryptoList(@PathVariable String status,
                                                                     Authentication authentication) {
        return ResponseWrapperDto.of(ResponseStatus.FAVORITE_CRYPTO_SUCCEED,
                favoriteCryptoService.findFavoriteCrypto(authentication));
    }

    @PostMapping
    public @ResponseBody ResponseWrapperDto favoriteCryptoAdd(@RequestBody FavoriteRequestDto favoriteRequestDto,
                                                              Authentication authentication,
                                                              BindingResult bindingResult) {
        // 유저 정보 조회
        Optional<UserAccount> userOpt = userService.findUserById(authentication);
        if (userOpt.isEmpty()) {
            return ResponseWrapperDto.of(ResponseStatus.USER_INFO_GET_FAILED);
        }
        UserAccount user = userOpt.get();

        // 즐겨찾기할 코인 정보 조회
        List<Crypto> cryptos = cryptoService.userFindCrypto(favoriteRequestDto.names());
        if (cryptos.isEmpty()) {
            return ResponseWrapperDto.of(ResponseStatus.CRYPTO_REQUEST_FAILED);
        }

        return ResponseWrapperDto.of(ResponseStatus.FAVORITE_CRYPTO_ADDED,
                favoriteCryptoService.addFavoriteCrypto(user, cryptos)
        );
    }
    
    @DeleteMapping
    public @ResponseBody ResponseWrapperDto favoriteCryptoRemove(@RequestBody FavoriteRequestDto favoriteRequestDto,
                                                                 Authentication authentication,
                                                                 BindingResult bindingResult) {
        favoriteCryptoService.removeFavoriteCrypto(favoriteRequestDto, authentication);
        return ResponseWrapperDto.of(ResponseStatus.CRYPTO_DELETE_SUCCEED,
                favoriteCryptoService.findFavoriteCrypto(authentication));
    }
}
