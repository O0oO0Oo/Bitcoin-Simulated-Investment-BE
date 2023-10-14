package com.cryptocurrency.investment.transaction.dto.processing;

/**
 * TransactionMysqlRepository 의 List<ReservedTransactionJpaDto> findByNameAndPriceAndStatusIsReservedForDto 에서 사용
 */
public interface ReservedTransactionJpaDto {
    Long getId();
    Double getPrice();
    Double getAmount();
    String getName();
    byte[] getUser_account_id();
    String getType();
}