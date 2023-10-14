package com.cryptocurrency.investment.wallet.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWallet is a Querydsl query type for Wallet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWallet extends EntityPathBase<Wallet> {

    private static final long serialVersionUID = -933060230L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWallet wallet = new QWallet("wallet");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final com.cryptocurrency.investment.crypto.domain.QCrypto crypto;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isRevenueDisclosed = createBoolean("isRevenueDisclosed");

    public final StringPath name = createString("name");

    public final NumberPath<Double> totalCost = createNumber("totalCost", Double.class);

    public final com.cryptocurrency.investment.user.domain.QUserAccount userAccount;

    public QWallet(String variable) {
        this(Wallet.class, forVariable(variable), INITS);
    }

    public QWallet(Path<? extends Wallet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWallet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWallet(PathMetadata metadata, PathInits inits) {
        this(Wallet.class, metadata, inits);
    }

    public QWallet(Class<? extends Wallet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crypto = inits.isInitialized("crypto") ? new com.cryptocurrency.investment.crypto.domain.QCrypto(forProperty("crypto")) : null;
        this.userAccount = inits.isInitialized("userAccount") ? new com.cryptocurrency.investment.user.domain.QUserAccount(forProperty("userAccount")) : null;
    }

}

