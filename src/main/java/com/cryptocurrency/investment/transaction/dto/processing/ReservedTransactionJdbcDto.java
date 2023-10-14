package com.cryptocurrency.investment.transaction.dto.processing;

import lombok.Data;

@Data
public class ReservedTransactionJdbcDto {
    private Long id;
    private Double price;
    private Double amount;
    private String name;
    private byte[] userAccountId;
    private String type;
}
