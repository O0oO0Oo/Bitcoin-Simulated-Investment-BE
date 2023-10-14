package com.cryptocurrency.investment.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAttendance is a Querydsl query type for UserAttendance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAttendance extends EntityPathBase<UserAttendance> {

    private static final long serialVersionUID = -1859940633L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAttendance userAttendance = new QUserAttendance("userAttendance");

    public final QAttendance attendance;

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUserAccount userAccount;

    public QUserAttendance(String variable) {
        this(UserAttendance.class, forVariable(variable), INITS);
    }

    public QUserAttendance(Path<? extends UserAttendance> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAttendance(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAttendance(PathMetadata metadata, PathInits inits) {
        this(UserAttendance.class, metadata, inits);
    }

    public QUserAttendance(Class<? extends UserAttendance> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.attendance = inits.isInitialized("attendance") ? new QAttendance(forProperty("attendance")) : null;
        this.userAccount = inits.isInitialized("userAccount") ? new QUserAccount(forProperty("userAccount")) : null;
    }

}

