package com.cryptocurrency.investment.crypto.service;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.domain.FavoriteCrypto;
import com.cryptocurrency.investment.crypto.dto.CryptoDto;
import com.cryptocurrency.investment.crypto.dto.request.FavoriteRequestDto;
import com.cryptocurrency.investment.crypto.repository.FavoriteCryptoRepository;
import com.cryptocurrency.investment.user.domain.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteCryptoService {
    private final FavoriteCryptoRepository favoriteCryptoRepository;

    public List<FavoriteCrypto> findFavoriteCrypto(Authentication authentication) {
        UUID id = UUID.fromString(authentication.getName());
        return favoriteCryptoRepository.findByUserAccount_Id(id);
    }

    public List<FavoriteCrypto> findFavoriteCrypto(Authentication authentication, String status) {
        UUID id = UUID.fromString(authentication.getName());
        return favoriteCryptoRepository.findByUserAccount_Id(id);
    }

    public List<FavoriteCrypto> addFavoriteCrypto(UserAccount userAccount,
                                                  List<Crypto> cryptos) {
        List<FavoriteCrypto> favoriteCryptos = cryptos.stream().map(
                crypto -> {
                    FavoriteCrypto favoriteCrypto = new FavoriteCrypto();
                    favoriteCrypto.setCrypto(crypto);
                    favoriteCrypto.setName(crypto.getName());
                    favoriteCrypto.setUserAccount(userAccount);
                    return favoriteCrypto;
                }
        ).collect(Collectors.toList());

        return favoriteCryptoRepository.saveAll(favoriteCryptos);
    }

    public boolean removeFavoriteCrypto(FavoriteRequestDto favoriteRequestDto,
                                        Authentication authentication){
        UUID id = UUID.fromString(authentication.getName());
        return favoriteCryptoRepository.deleteByUserAccount_IdAndNameIn(id, favoriteRequestDto.names());
    }
}