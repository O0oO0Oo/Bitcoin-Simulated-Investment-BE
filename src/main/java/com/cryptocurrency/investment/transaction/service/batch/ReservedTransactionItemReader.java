package com.cryptocurrency.investment.transaction.service.batch;

import com.cryptocurrency.investment.price.dto.scheduler.PriceInfoDto;
import com.cryptocurrency.investment.price.util.PriceMessageQueue;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReservedTransactionItemReader<T> extends AbstractPagingItemReader<T> implements InitializingBean {
    private static final String START_AFTER_VALUE = "start.after";
    private PagingQueryProvider queryProvider;
    private Map<String, Object> parameterValues;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private RowMapper<T> rowMapper;

    private String firstPageSql;
    private String remainingPagesSql;

    private Map<String, Object> startAfterValues;
    private Map<String, Object> previousStartAfterValues;
    private DataSource dataSource;

    public static final int VALUE_NOT_SET = -1;
    private int fetchSize = VALUE_NOT_SET;

    // 검색할 쿼리
    private String iteratorSql;
    /**
     * 검색할 데이터들 저장
     */
    private int iteratorIndex = 0;

    private List<String> iteratorValues;
    private String iteratorColumnName;
    /**
     * 추후에 멀티모듈이나 MSA 로 분할시 다른 MessageQueue 를 사용하기 위해 인터페이스로 분리
     */
    private PriceMessageQueue priceMessageQueue;

    /**
     * 가격을 받아오기 위한 PriceMessageQueue 의 구현체를 세팅
     * @param priceMessageQueue
     */
    public void setPriceMessageQueue(PriceMessageQueue priceMessageQueue) {
        this.priceMessageQueue = priceMessageQueue;
    }

    /**
     * Iterator 로 불러올 데이터의 쿼리
     * @param iteratorSql
     */
    public void setIteratorSql(String iteratorSql) {
        this.iteratorSql = iteratorSql;
    }

    /**
     * 수정된 부분
     * 코인목록을 불러오는 Iterator 의 초기화와, where 절의 변수에 들어갈 parameterValue 초기화와 삽입
     *
     * 매 open 시 목록을 불러오는 이유는, 특정 코인의 상태가 거래불가능한 상태로 바뀔수도 있기떄문에.
     *
     * @param executionContext current step's
     * {@link org.springframework.batch.item.ExecutionContext}. Will be the
     * executionContext from the last run of the step on a restart.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void open(ExecutionContext executionContext) {
        // Iterator 설정
        iteratorValues = new LinkedList<>();
        parameterValues = new HashMap<>();
        getIterator();

        if (isSaveState()) {
            startAfterValues = (Map<String, Object>) executionContext.get(getExecutionContextKey(START_AFTER_VALUE));
            if (startAfterValues == null) {
                startAfterValues = new LinkedHashMap<>();
            }
        }
        super.open(executionContext);
    }

    // 이름을 받아서 MessageQueue 에서 데이터를 받아오고 쿼리에 넣을 Parameter 를 넣는다
    private boolean inputParameterValues(String iteratorColumnName) {
        PriceInfoDto consume = priceMessageQueue.consume(iteratorColumnName);
        // consume 에 없다면 다음 것으로 넘어감
        while ((consume == null) && (iteratorIndex < iteratorValues.size())){
            consume = priceMessageQueue.consume(iteratorColumnName);
            iteratorColumnName = iteratorValues.get(++iteratorIndex);
        }

        if (consume == null) {
            return true;
        }
        else {
            parameterValues.put("name", consume.getName());
            parameterValues.put("price", consume.getPrice());
            parameterValues.put("timestamp", consume.getTimestamp());
            return false;
        }
    }

    /**
     * @param iteratorColumnName 실행한 쿼리에서 Iterator 로 사용할 컬럼 이름
     */
    public void setIteratorColumnName(String iteratorColumnName) {
        this.iteratorColumnName = iteratorColumnName;
    }

    /**
     * TODO : 현재는 목록의 1000개까지 가능하도록 했지만 추후에 Paging 기능 추가
     * 최대 1000개의 Iterator 을 불러옴, 그 이후 다시 Max Row 재설정
     */
    private void getIterator() {
        // 최대 1000개 목록까지 가능
        getJdbcTemplate().setMaxRows(1000);
        List<Map<String, Object>> resultList = getJdbcTemplate().queryForList(iteratorSql);
        System.out.println("resultList.size() = " + resultList.size());
        for (Map<String, Object> row : resultList) {
            iteratorValues.add(row.get(iteratorColumnName).toString());
        }
        getJdbcTemplate().setMaxRows(getPageSize());
    }

    /**
     * 예약된 트랙잭션의 목록을 가져오는 쿼리는
     * where name = ?, price = ?, timestamp <= ?
     * 예약거래로 등록된 코인 이름, 코인 가격 그리고 예약 거래로 등록된 시간
     * (0시 0분 10초에 100원으로 예약된 거래가 있다면, reader 가 부하로 인해 거래작업이 지연되어 0시 0분 5초에 100원 가격의 처리가 0분 15초에 시작된경우를 막기위해 넣었다)
     *
     * 기존의 reader 의 경우 QueryProvider 로 where name = ?, price = ? 을 미리 설정해 놓기때문에
     * 하나의 스텝에서 chunk 사이즈를 100으로 설정시 100가지 종류의 코인이 각 1개의 예약된 거래가 있다면 100개의 예약된 거래를 처리하기 위해 100번의 스텝(Reader - Processor - Writer)을 실행해야했다.
     *
     * 이 부분을 처리하기 위해 Chunk 사이즈에 도달할때까지 다음 코인의 예약된 거래목록을 불러온다.
     */
    @Override
    protected void doReadPage() {
        if (results == null) {
            results = new CopyOnWriteArrayList<>();
        }
        else {
            results.clear();
        }

        // Consume 이 모두 null 일경우 종료하도록 하기위해서
        boolean isConsumeNull = inputParameterValues(iteratorValues.get(iteratorIndex));
        PagingRowMapper rowCallback = new PagingRowMapper();
        List<T> query = new ArrayList<>();

        if (getPage() == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("SQL used for reading first page: [" + firstPageSql + "]");
            }
            while (query.size() < getPageSize()) {
                if (isConsumeNull) {
                    break;
                }

                // -------------- 반복되어야 할 부분 --------------
                // 만약 반복될 시 쿼리 limit 의 수정이 필요함
                // ex) pageSize 100 설정, BTC 40 - ETH 100(설정을 안할시 limit 100) -> ETH(60) 이 되어야함
                // limit 는 getPageSize() - query.size()(BTC) 가 되어야하고 이후에는 복구 되어야한다.
                if (parameterValues != null && parameterValues.size() > 0) {
                    if (this.queryProvider.isUsingNamedParameters()) {
                        query.addAll(namedParameterJdbcTemplate.query(
                                firstPageSql,
                                getParameterMap(parameterValues, null),
                                rowCallback));
                    } else {
                        query.addAll(getJdbcTemplate().query(firstPageSql, rowCallback,
                                getParameterList(parameterValues, null).toArray()));
                    }
                } else {
                    query.addAll(getJdbcTemplate().query(firstPageSql, rowCallback));
                }
                // -------------- 반복되어야 할 부분 --------------

                // query 를 가져왔지만, pageSize 보다 작을 떄.
                if(query.size() < getPageSize()){
                    // iteratorIndex 를 1 증가, 다음 코인에 대한 가격을 검색
                    iteratorIndex++;
                    
                    // iteratorPageSize 에 따라 불러온 마지막이면 루프를 끝냄
                    if(iteratorIndex >= iteratorValues.size()){
                        iteratorIndex = 0;
                        iteratorValues.clear();
                        break;
                    }
                    else {
                        // 다음 Iterator 를 불러오고 다시 반복한다.
                        isConsumeNull = inputParameterValues(iteratorValues.get(iteratorIndex));
                    }
                    // 다음 Iterator 를 불러오고 다시 반복한다. 하지만 limit 는 getPageSize() - query.size() 가 되어야하고 이후에는 복구 되어야한다.
                    firstPageSql = queryProvider.generateFirstPageQuery(getPageSize() - query.size());
                }
            }
        }

        else if (startAfterValues != null) {

            // query 의 사이즈가 page 사이즈에 도달 시 중지
            while (query.size() < getPageSize()){
                if (isConsumeNull) {
                    break;
                }

                // -------------- 반복되어야 할 부분 --------------
                previousStartAfterValues = startAfterValues;
                if (logger.isDebugEnabled()) {
                    logger.debug("SQL used for reading remaining pages: [" + remainingPagesSql + "]");
                }
                if (this.queryProvider.isUsingNamedParameters()) {
                    query.addAll(namedParameterJdbcTemplate.query(remainingPagesSql,
                            getParameterMap(parameterValues, startAfterValues), rowCallback));
                }
                else {
                    query.addAll(getJdbcTemplate().query(remainingPagesSql, rowCallback,
                            getParameterList(parameterValues, startAfterValues).toArray()));
                }
                // -------------- 반복되어야 할 부분 --------------

                // query 를 가져옴 하지만 pageSize 보다 작을떄.
                if(query.size() < getPageSize()){
                    // iteratorIndex 를 1 증가, 마지막이면 다시 iterator 를 불러와야함.
                    iteratorIndex++;
                    // id 도 where id > 0 부터 시작할 수 있도록 초기화
                    startAfterValues.put("id", 0);
                    if(iteratorIndex >= iteratorValues.size()){
                        iteratorIndex = 0;
                        iteratorValues.clear();
                        break;
                    }
                    else {
                        isConsumeNull = inputParameterValues(iteratorValues.get(iteratorIndex));
                    }
                    // 다음 Iterator 를 불러오고 다시 반복한다. 하지만 limit 는 getPageSize() - query.size() 가 되어야하고 이후에는 복구 되어야한다.
                    remainingPagesSql = queryProvider.generateRemainingPagesQuery(getPageSize() - query.size());
                }
            }
        }
        else {
            query = Collections.emptyList();
        }
        results.addAll(query);
    }

    /**
     * page 사이즈만큼 query 가 채워지고 Reader 가 끝나면 다시 Limit N 을 pageSIze 로 복구한다.
     * @param executionContext to be updated
     * @throws ItemStreamException
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);

        if (isSaveState()) {
            if (isAtEndOfPage() && startAfterValues != null) {
                // restart on next page
                executionContext.put(getExecutionContextKey(START_AFTER_VALUE), startAfterValues);
            }
            else if (previousStartAfterValues != null) {
                // restart on current page
                executionContext.put(getExecutionContextKey(START_AFTER_VALUE), previousStartAfterValues);
            }
        }

        // 쿼리의 pageSize 복구
        this.firstPageSql = queryProvider.generateFirstPageQuery(getPageSize());
        this.remainingPagesSql = queryProvider.generateRemainingPagesQuery(getPageSize());
    }

    @Override
    public void close() throws ItemStreamException {
        super.close();

        // 초기화
        iteratorIndex = 0;
    }

    private boolean isAtEndOfPage() {
        return getCurrentItemCount() % getPageSize() == 0;
    }
    private Map<String, Object> getParameterMap(Map<String, Object> values, Map<String, Object> sortKeyValues) {
        Map<String, Object> parameterMap = new LinkedHashMap<>();
        if (values != null) {
            parameterMap.putAll(values);
        }
        if (sortKeyValues != null && !sortKeyValues.isEmpty()) {
            for (Map.Entry<String, Object> sortKey : sortKeyValues.entrySet()) {
                parameterMap.put("_" + sortKey.getKey(), sortKey.getValue());
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Using parameterMap:" + parameterMap);
        }
        return parameterMap;
    }
    private List<Object> getParameterList(Map<String, Object> values, Map<String, Object> sortKeyValue) {
        SortedMap<String, Object> sm = new TreeMap<>();
        if (values != null) {
            sm.putAll(values);
        }

        List<Object> parameterList = new ArrayList<>();
        parameterList.addAll(sm.values());
        if (sortKeyValue != null && sortKeyValue.size() > 0) {
            List<Map.Entry<String, Object>> keys = new ArrayList<>(sortKeyValue.entrySet());

            for (int i = 0; i < keys.size(); i++) {
                for (int j = 0; j < i; j++) {
                    parameterList.add(keys.get(j).getValue());
                }

                parameterList.add(keys.get(i).getValue());
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Using parameterList:" + parameterList);
        }
        return parameterList;
    }
    private class PagingRowMapper implements RowMapper<T> {

        @Override
        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
            startAfterValues = new LinkedHashMap<>();
            for (Map.Entry<String, Order> sortKey : queryProvider.getSortKeys().entrySet()) {
                startAfterValues.put(sortKey.getKey(), rs.getObject(sortKey.getKey()));
            }
            return rowMapper.mapRow(rs, rowNum);
        }
    }
    private JdbcTemplate getJdbcTemplate() {
        return (JdbcTemplate) namedParameterJdbcTemplate.getJdbcOperations();
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.state(dataSource != null, "DataSource may not be null");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        if (fetchSize != VALUE_NOT_SET) {
            jdbcTemplate.setFetchSize(fetchSize);
        }
        jdbcTemplate.setMaxRows(getPageSize());
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Assert.state(queryProvider != null, "QueryProvider may not be null");
        queryProvider.init(dataSource);
        this.firstPageSql = queryProvider.generateFirstPageQuery(getPageSize());
        this.remainingPagesSql = queryProvider.generateRemainingPagesQuery(getPageSize());

        // priceMessageQueue 가 있어야 함.
        Assert.state(priceMessageQueue != null, "PriceMessage may not be null");
    }
    public void setQueryProvider(PagingQueryProvider queryProvider) {
        this.queryProvider = queryProvider;
    }
    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}

