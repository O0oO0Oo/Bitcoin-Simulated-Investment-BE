package com.cryptocurrency.investment.crypto.service;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import com.cryptocurrency.investment.crypto.dto.CryptoDto;
import com.cryptocurrency.investment.crypto.dto.CryptoModifyDto;
import com.cryptocurrency.investment.crypto.repository.CryptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Crypto> userFindStatusCrypto(CryptoStatus status) {
        return cryptoRepository.findByStatus(status);
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
