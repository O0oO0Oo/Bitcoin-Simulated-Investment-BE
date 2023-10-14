package com.cryptocurrency.investment.transaction.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTransaction is a Querydsl query type for Transaction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTransaction extends EntityPathBase<Transaction> {

    private static final long serialVersionUID = 1771613392L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTransaction transaction = new QTransaction("transaction");

    public final NumberPath<Double> amount = createNumber("amount", Double.class);

    public final com.cryptocurrency.investment.crypto.domain.QCrypto crypto;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Double> price = createNumber("price", Double.class);

    public final EnumPath<TransactionStatus> status = createEnum("status", TransactionStatus.class);

    public final NumberPath<Long> timestamp = createNumber("timestamp", Long.class);

    public final EnumPath<TransactionType> type = createEnum("type", TransactionType.class);

    public final com.cryptocurrency.investment.user.domain.QUserAccount userAccount;

    public QTransaction(String variable) {
        this(Transaction.class, forVariable(variable), INITS);
    }

    public QTransaction(Path<? extends Transaction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTransaction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTransaction(PathMetadata metadata, PathInits inits) {
        this(Transaction.class, metadata, inits);
    }

    public QTransaction(Class<? extends Transaction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crypto = inits.isInitialized("crypto") ? new com.cryptocurrency.investment.crypto.domain.QCrypto(forProperty("crypto")) : null;
        this.userAccount = inits.isInitialized("userAccount") ? new com.cryptocurrency.investment.user.domain.QUserAccount(forProperty("userAccount")) : null;
    }

}

