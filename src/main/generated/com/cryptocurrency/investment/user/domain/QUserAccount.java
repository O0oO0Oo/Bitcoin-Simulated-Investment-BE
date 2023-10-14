package com.cryptocurrency.investment.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAccount is a Querydsl query type for UserAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAccount extends EntityPathBase<UserAccount> {

    private static final long serialVersionUID = 925994735L;

    public static final QUserAccount userAccount = new QUserAccount("userAccount");

    public final ListPath<UserAttendance, QUserAttendance> attendances = this.<UserAttendance, QUserAttendance>createList("attendances", UserAttendance.class, QUserAttendance.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final ListPath<com.cryptocurrency.investment.crypto.domain.FavoriteCrypto, com.cryptocurrency.investment.crypto.domain.QFavoriteCrypto> favoriteCryptos = this.<com.cryptocurrency.investment.crypto.domain.FavoriteCrypto, com.cryptocurrency.investment.crypto.domain.QFavoriteCrypto>createList("favoriteCryptos", com.cryptocurrency.investment.crypto.domain.FavoriteCrypto.class, com.cryptocurrency.investment.crypto.domain.QFavoriteCrypto.class, PathInits.DIRECT2);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final BooleanPath isSuspended = createBoolean("isSuspended");

    public final DatePath<java.time.LocalDate> joinDate = createDate("joinDate", java.time.LocalDate.class);

    public final NumberPath<Double> money = createNumber("money", Double.class);

    public final StringPath password = createString("password");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final DatePath<java.time.LocalDate> suspensionDate = createDate("suspensionDate", java.time.LocalDate.class);

    public final ListPath<com.cryptocurrency.investment.transaction.domain.Transaction, com.cryptocurrency.investment.transaction.domain.QTransaction> transactions = this.<com.cryptocurrency.investment.transaction.domain.Transaction, com.cryptocurrency.investment.transaction.domain.QTransaction>createList("transactions", com.cryptocurrency.investment.transaction.domain.Transaction.class, com.cryptocurrency.investment.transaction.domain.QTransaction.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public final ListPath<com.cryptocurrency.investment.wallet.domain.Wallet, com.cryptocurrency.investment.wallet.domain.QWallet> wallets = this.<com.cryptocurrency.investment.wallet.domain.Wallet, com.cryptocurrency.investment.wallet.domain.QWallet>createList("wallets", com.cryptocurrency.investment.wallet.domain.Wallet.class, com.cryptocurrency.investment.wallet.domain.QWallet.class, PathInits.DIRECT2);

    public QUserAccount(String variable) {
        super(UserAccount.class, forVariable(variable));
    }

    public QUserAccount(Path<? extends UserAccount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAccount(PathMetadata metadata) {
        super(UserAccount.class, metadata);
    }

}

