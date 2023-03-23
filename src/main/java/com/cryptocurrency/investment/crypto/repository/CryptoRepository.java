package com.cryptocurrency.investment.crypto.repository;

import com.cryptocurrency.investment.crypto.domain.Crypto;
import com.cryptocurrency.investment.crypto.domain.CryptoStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CryptoRepository extends JpaRepository<Crypto,Long> {

    /**
     * User
     */
    List<Crypto> findByStatus(CryptoStatus status);

    @Query(value = "SELECT * " +
            "FROM crypto c " +
            "WHERE c.status != 'NOT_USED' or c.status != 'DELETED'"
            , nativeQuery = true)
    List<Crypto> findAllExceptStatus();

    @Query(value = "SELECT * " +
            "FROM (" +
            "SELECT  * FROM crypto WHERE name = :name) as c " +
            "WHERE c.status != 'NOT_USED' or c.status != 'DELETED'"
            , nativeQuery = true)
    Optional<Crypto> findByNameExceptStatus(@Param("name") String name);

    List<Crypto> findByNameIn(List<String> names);

    /**
     * Admin
     */

    Boolean existsByName(String name);

    @Modifying
    @Transactional
    @Query(value = "UPDATE crypto c " +
            "SET c.name = :updateName, c.status = :status " +
            "WHERE c.name = :name", nativeQuery = true)
    int updateCrypto(@Param("name") String name,
                   @Param("updateName") String updateName,
                   @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE crypto c " +
            "SET c.status = :status " +
            "WHERE c.name = :name", nativeQuery = true)
    int deleteCrypto(@Param("name") String name,
                     @Param("status") String status);

}
