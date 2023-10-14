package com.cryptocurrency.investment.transaction.repository.redis;

import com.cryptocurrency.investment.transaction.domain.Transaction;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.util.List;


/**
 * 자료구조
 * List
 * 
 * 옵션
 * list-compress-depth 1
 *
 * key -> Name:[Sell|Buy]:Price
 * Value -> OrderId:UserId:Amount
 */
@Repository
@RequiredArgsConstructor
public class TransactionRedisRepository {
    private final RedisConnectionFactory redisConnectionFactory;
    private DecimalFormat decimalFormat;

    @PostConstruct
    public void init() {
        decimalFormat = new DecimalFormat("#");
        decimalFormat.setMaximumFractionDigits(340);
    }

    /**
     * 트랜잭션 Redis 에 저장
     * @param transactions
     */
    public void saveAll(List<Transaction> transactions) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.openPipeline();
        transactions.stream().parallel().forEach(
                t -> {
                    connection.listCommands().rPush(
                            (t.getName()+ ":" + decimalFormat.format(t.getPrice())).getBytes(),
                            (t.getId() + ":" + decimalFormat.format(t.getPrice()) + ":" + t.getUserAccount().getId()
                                    + ":" + t.getType()).getBytes()
                    );
                }
        );
        connection.closePipeline();
        connection.close();
    }

    /**
     * List 구조 RPUSH O(1)
     * key : Name:[Sell|Buy]:Price
     * Val : OrderId:UserId:Amount
     * @param transaction 주문 거래 데이터
     * @return 저장 완료시 1 반환
     */
    public Long save(Transaction transaction) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        Long savedN = connection.listCommands().rPush(
                (transaction.getName() + ":" + transaction.getType() + ":" + decimalFormat.format(transaction.getPrice())).getBytes(),
                (transaction.getId() + ":" + transaction.getUserAccount().getId() + ":" + transaction.getAmount()).getBytes()
        );
        connection.close();
        return savedN;
    }

    /**
     * Transaction 을 받아서 삭제
     * @return 삭제 완료시 1 반환
     */
    public Long delete(Transaction transaction) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        Long deletedN = connection.listCommands().lRem(
                (transaction.getName() + ":" + transaction.getType() + ":" + decimalFormat.format(transaction.getPrice())).getBytes(),
                1,
                (transaction.getId() + ":" + transaction.getUserAccount().getId() + ":" + transaction.getAmount()).getBytes()
        );
        connection.close();
        return deletedN;
    }
}
