package com.cryptocurrency.investment.crypto.service;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import com.cryptocurrency.investment.crypto.dto.CryptoDto;
import com.cryptocurrency.investment.crypto.dto.CryptoModifyDto;
import com.cryptocurrency.investment.crypto.repository.CryptoRepository;
import com.cryptocurrency.investment.transaction.dto.request.TransactionRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoService {
    private final CryptoRepository cryptoRepository;

    /**
     * User
     */
    public List<Crypto> userFindCrypto() {
        return cryptoRepository.findAllExceptStatus();
    }

    public List<Crypto> userFindCrypto(List<String> names) {
        List<String> upperCaseNames = names.stream().map(
                name -> name.toUpperCase()
        ).toList();
        return cryptoRepository.findByNameIn(upperCaseNames);
    }

    public List<Crypto> userFindStatusCrypto(CryptoStatus status) {
        return cryptoRepository.findByStatus(status);
    }

    public Optional<Crypto> userFindStatusCrypto(TransactionRequestDto txDto){
        return cryptoRepository.findByNameExceptStatus(txDto.name());
    }
    /**
     * Admin
     */
    public List<Crypto> adminFindCrypto() {
        return cryptoRepository.findAll();
    }

    public Boolean adminFindCrypto(CryptoDto cryptoDto) {
        return cryptoRepository.existsByName(cryptoDto.name().toUpperCase());
    }

    public Boolean adminFindCrypto(CryptoModifyDto modifyDto) {
        return cryptoRepository.existsByName(modifyDto.name().toUpperCase());
    }

    public Boolean adminFindCrypto(String name) {
        return cryptoRepository.existsByName(name.toUpperCase());
    }

    public Crypto addCrypto(CryptoDto cryptoDto) {
        return cryptoRepository.save(
                new Crypto(cryptoDto.name().toUpperCase(), cryptoDto.status())
        );
    }

    public int modifyCrypto(CryptoModifyDto modifyDto) {
        return cryptoRepository.updateCrypto(
                modifyDto.name().toUpperCase(),
                modifyDto.newName().toUpperCase(),
                modifyDto.newStatus().toString());
    }

    public int removeCrypto(CryptoDto cryptoDto) {
        return cryptoRepository.deleteCrypto(
                cryptoDto.name().toUpperCase(),
                CryptoStatus.DELETED.toString());
    }
}
