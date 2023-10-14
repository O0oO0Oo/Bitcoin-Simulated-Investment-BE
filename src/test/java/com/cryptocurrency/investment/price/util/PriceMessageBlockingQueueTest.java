package com.cryptocurrency.investment.price.util;

import com.cryptocurrency.investment.price.dto.request.RequestPriceInfoDto;
import com.cryptocurrency.investment.price.dto.scheduler.PriceInfoDto;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(MockitoExtension.class)
class PriceMessageBlockingQueueTest {

    @InjectMocks
    private PriceMessageBlockingQueue priceMessageBlockingQueue;
    @InjectMocks
    private HttpJsonRequest httpJsonRequest;
    RequestPriceInfoDto readValue;

    // 가격을 불러왔을때 consume 메서드가 실행되도록 트리거
    AtomicBoolean trigger = new AtomicBoolean(false);
    // produce 메서드가 끝남을 알림
    volatile boolean done = true;

    // 0.1초마다 가격정보를 불러오고 큐에 넣음
    class Produce implements Callable<Integer> {
        private Integer count = 0;
        private int sleep;

        public Produce(int count) {
            this.sleep = count;
        }

        @Override
        public Integer call() {
            for (int i = 0; i < 50; i++) {
                try {
                    readValue = httpJsonRequest.sendRequest("https://api.bithumb.com/public/ticker/ALL", RequestPriceInfoDto.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                trigger.set(true);
                priceMessageBlockingQueue.produce(readValue.getFields(), readValue.getInnerData().getTimestamp());
                count++;
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            done = false;
            return count;
        }
    }

    // 큐마다 소비된 갯수를 카운트
    ConcurrentHashMap<String, Integer> consumeCount = new ConcurrentHashMap<>();

    // 가격들을 큐에서 꺼내서 처리
    class Consume implements Callable<Integer> {
        private static AtomicInteger count = new AtomicInteger(0);

        @Override
        public Integer call() throws InterruptedException {
            /**
             * 레이스 컨디션 문제인지.. 섞어서 실행시 조금더 빠르다
             * 데이터 불러오는 시간을 생각하면 선형적으로 실행될거같은데??
             */
            LinkedList<String> strings = new LinkedList<>(readValue.getFields().keySet());
            Collections.shuffle(strings);
            for (String key : strings) {
                consumeCount.computeIfAbsent(key, k -> 0);
                PriceInfoDto consume = priceMessageBlockingQueue.consume(key);
                if (consume != null) {
                    consumeCount.computeIfPresent(key,
                            (k, v) -> v + 1);
                    count.incrementAndGet();
                }
            }
            return count.get();
        }
    }

    @Test
    @DisplayName("10초간 0.1초마다 Produce -> Consume(4개의 Thread) 테스트")
    void produceAndConsume_when10SecondsRequestData_thenConsume4Threads() throws InterruptedException, ExecutionException {
        // when - Produce - 10SecondsRequestData
        ExecutorService produceExecutorService = Executors.newSingleThreadExecutor();
        Future<Integer> produceSubmit = produceExecutorService.submit(new Produce(100));

        // then - Consume - Consume4Threads
        ExecutorService consumeExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (done) {
            // 가격을 받아오면 실행
            if (trigger.getAndSet(false)) {
                consumeExecutorService.submit(new Consume());
            }
        }
        System.out.println("Produce count : " + produceSubmit.get());
        System.out.println("Consume count : " + Consume.count.get());
        System.out.println("코인 종류 : " + readValue.getFields().keySet().size());


        /**
         * 중복된 가격들 n초 단위로 합쳐서 처리 이후 테스트 결과가 달라짐
         * ex) consumeCount.computeIfPresent(key,
         *                             (k, v) -> v+1); 동일 가격이 10개 처리되어도 소비 카운트 1 증가
         *                             
         * 0초로 설정시 테스트 통과
         */
        List<Integer> sizeList = new ArrayList<>();
        for (String s : readValue.getFields().keySet()) {
            if (!consumeCount.get(s).equals(produceSubmit.get() - priceMessageBlockingQueue.size(s))) {
//                System.out.println("소비 != 생산 - 큐에 남아있는 수 가 같지않다. " + s + "의 소비 : " +
//                        "" + consumeCount.get(s) + " 생산 : " + produceSubmit.get() + "" +
//                        " 큐에 남아있는 수 : " + priceMessageBlockingQueue.size(s));
                Assertions.fail("소비 != 생산 - 큐에 남아있는 수 가 같지않다. " + s + "의 소비 : " +
                        "" + consumeCount.get(s) + " 생산 : " + produceSubmit.get() + "" +
                        " 큐에 남아있는 수 : " + priceMessageBlockingQueue.size(s));
            }
            sizeList.add(priceMessageBlockingQueue.size(s));
        }

        System.out.println("끝");
        produceExecutorService.shutdown();
        consumeExecutorService.shutdown();
    }
    
    @Test
    @DisplayName("0.01초마다 요청 Produce -> 스레드 세이프한지")
    void produce_when10SecondsRequestData_thenThreadSafeTest() throws InterruptedException {
        ExecutorService produceExecutorService = Executors.newSingleThreadExecutor();

        // block 당할지도 모르니 0.01초 간격 50번만
        produceExecutorService.submit(new Produce(10));

        // 요청이 끝날떄까지
        while (true) {
            if(!done){
                break;
            }
        }
        produceExecutorService.shutdown();
        for (String s : readValue.getFields().keySet()) {
            if (priceMessageBlockingQueue.size(s) != 50) {
                Assertions.fail("사이즈는 50이 되어야함");
            }
        }
    }
}