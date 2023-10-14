package com.cryptocurrency.investment.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmailValidation is a Querydsl query type for EmailValidation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmailValidation extends EntityPathBase<EmailValidation> {

    private static final long serialVersionUID = -629116478L;

    public static final QEmailValidation emailValidation = new QEmailValidation("emailValidation");

    public final NumberPath<Integer> code = createNumber("code", Integer.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public QEmailValidation(String variable) {
        super(EmailValidation.class, forVariable(variable));
    }

    public QEmailValidation(Path<? extends EmailValidation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmailValidation(PathMetadata metadata) {
        super(EmailValidation.class, metadata);
    }

}

