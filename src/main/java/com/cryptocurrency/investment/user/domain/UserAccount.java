package com.cryptocurrency.investment.user.domain;

import com.cryptocurrency.investment.crypto.domain.FavoriteCrypto;
import com.cryptocurrency.investment.transaction.domain.Transaction;
import com.cryptocurrency.investment.wallet.domain.Wallet;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.Range;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * TODO : UUID 를 Pk 로 사용하면 성능상 문제가 발생
 * @Data
 * @Entity
 * @NoArgsConstructor
 * @Table(name = "user_account",
 *         indexes = @Index(name = "idx_uuid", columnList = "id", unique = true))
 * public class UserAccount {
 *     @Id
 *     @GeneratedValue(strategy = GenerationType.IDENTITY)
 *     private Long pk;
 *     @Column(columnDefinition = "BINARY(16)")
 *
 *     로 교환하기
 */
@Data
@Entity
@NoArgsConstructor
public class UserAccount {
    @Id
    private UUID id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    private LocalDate joinDate = LocalDate.now();
    @OneToMany(mappedBy = "userAccount")
    private List<UserAttendance> attendances = new ArrayList<>();
    private boolean isDeleted = false;
    private boolean isSuspended = false;
    private LocalDate suspensionDate = LocalDate.now();
    @OneToMany(mappedBy = "userAccount")
    private List<Wallet> wallets = new ArrayList<>();
    @OneToMany(mappedBy = "userAccount")
    private List<Transaction> transactions = new ArrayList<>();
    private double money = 10000000.0;
    @OneToMany(mappedBy = "userAccount")
    private List<FavoriteCrypto> favoriteCryptos = new ArrayList<>();
}