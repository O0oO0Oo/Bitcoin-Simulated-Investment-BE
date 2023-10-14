package com.cryptocurrency.investment.crypto.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFavoriteCrypto is a Querydsl query type for FavoriteCrypto
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFavoriteCrypto extends EntityPathBase<FavoriteCrypto> {

    private static final long serialVersionUID = -282749754L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFavoriteCrypto favoriteCrypto = new QFavoriteCrypto("favoriteCrypto");

    public final QCrypto crypto;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final com.cryptocurrency.investment.user.domain.QUserAccount userAccount;

    public QFavoriteCrypto(String variable) {
        this(FavoriteCrypto.class, forVariable(variable), INITS);
    }

    public QFavoriteCrypto(Path<? extends FavoriteCrypto> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFavoriteCrypto(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFavoriteCrypto(PathMetadata metadata, PathInits inits) {
        this(FavoriteCrypto.class, metadata, inits);
    }

    public QFavoriteCrypto(Class<? extends FavoriteCrypto> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.crypto = inits.isInitialized("crypto") ? new QCrypto(forProperty("crypto")) : null;
        this.userAccount = inits.isInitialized("userAccount") ? new com.cryptocurrency.investment.user.domain.QUserAccount(forProperty("userAccount")) : null;
    }

}

