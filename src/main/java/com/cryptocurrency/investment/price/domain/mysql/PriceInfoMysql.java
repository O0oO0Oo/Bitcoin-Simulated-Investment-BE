package com.cryptocurrency.investment.price.domain.mysql;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Table(indexes = {
        @Index(columnList = "name, timestamp")
})
@Entity
@NoArgsConstructor
public class PriceInfoMysql {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private String price;

    private String maxPrice;

    private String minPrice;

    public PriceInfoMysql(String name, Long timestamp, String curPrice, String maxPrice, String minPrice) {
        this.name = name;
        this.timestamp = timestamp;
        this.price = curPrice;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }
}