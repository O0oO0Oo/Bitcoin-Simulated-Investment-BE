package com.cryptocurrency.investment.price.domain.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Table(indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "timestamp")
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
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String price;
}
