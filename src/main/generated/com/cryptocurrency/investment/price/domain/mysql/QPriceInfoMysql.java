package com.cryptocurrency.investment.price.domain.mysql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPriceInfoMysql is a Querydsl query type for PriceInfoMysql
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPriceInfoMysql extends EntityPathBase<PriceInfoMysql> {

    private static final long serialVersionUID = -623626928L;

    public static final QPriceInfoMysql priceInfoMysql = new QPriceInfoMysql("priceInfoMysql");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> maxPrice = createNumber("maxPrice", Double.class);

    public final NumberPath<Double> minPrice = createNumber("minPrice", Double.class);

    public final StringPath name = createString("name");

    public final NumberPath<Double> price = createNumber("price", Double.class);

    public final NumberPath<Long> timestamp = createNumber("timestamp", Long.class);

    public QPriceInfoMysql(String variable) {
        super(PriceInfoMysql.class, forVariable(variable));
    }

    public QPriceInfoMysql(Path<? extends PriceInfoMysql> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPriceInfoMysql(PathMetadata metadata) {
        super(PriceInfoMysql.class, metadata);
    }

}

