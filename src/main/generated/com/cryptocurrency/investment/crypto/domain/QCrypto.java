package com.cryptocurrency.investment.crypto.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCrypto is a Querydsl query type for Crypto
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCrypto extends EntityPathBase<Crypto> {

    private static final long serialVersionUID = -348791542L;

    public static final QCrypto crypto = new QCrypto("crypto");

    public final ListPath<FavoriteCrypto, QFavoriteCrypto> favoriteCryptos = this.<FavoriteCrypto, QFavoriteCrypto>createList("favoriteCryptos", FavoriteCrypto.class, QFavoriteCrypto.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final EnumPath<CryptoStatus> status = createEnum("status", CryptoStatus.class);

    public final ListPath<com.cryptocurrency.investment.transaction.domain.Transaction, com.cryptocurrency.investment.transaction.domain.QTransaction> transactions = this.<com.cryptocurrency.investment.transaction.domain.Transaction, com.cryptocurrency.investment.transaction.domain.QTransaction>createList("transactions", com.cryptocurrency.investment.transaction.domain.Transaction.class, com.cryptocurrency.investment.transaction.domain.QTransaction.class, PathInits.DIRECT2);

    public final ListPath<com.cryptocurrency.investment.wallet.domain.Wallet, com.cryptocurrency.investment.wallet.domain.QWallet> wallets = this.<com.cryptocurrency.investment.wallet.domain.Wallet, com.cryptocurrency.investment.wallet.domain.QWallet>createList("wallets", com.cryptocurrency.investment.wallet.domain.Wallet.class, com.cryptocurrency.investment.wallet.domain.QWallet.class, PathInits.DIRECT2);

    public QCrypto(String variable) {
        super(Crypto.class, forVariable(variable));
    }

    public QCrypto(Path<? extends Crypto> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCrypto(PathMetadata metadata) {
        super(Crypto.class, metadata);
    }

}

