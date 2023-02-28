package com.cryptocurrency.investment.price.domain.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private double price;

    private double maxPrice;

    private double minPrice;

    public PriceInfoMysql(String name, Long timestamp, double curPrice, double maxPrice, double minPrice) {
        this.name = name;
        this.timestamp = timestamp;
        this.price = curPrice;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
    }
}