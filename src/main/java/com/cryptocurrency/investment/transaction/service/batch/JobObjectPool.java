package com.cryptocurrency.investment.transaction.service.batch;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;

import java.util.NoSuchElementException;

public class JobObjectPool<T> implements ObjectPool<T> {

    private static int count = 0;
    @Override
    public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {

    }

    @Override
    public T borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
        return null;
    }

    @Override
    public void clear() throws Exception, UnsupportedOperationException {

    }

    @Override
    public void close() {

    }

    @Override
    public int getNumActive() {
        return 0;
    }

    @Override
    public int getNumIdle() {
        return 0;
    }

    @Override
    public void invalidateObject(T obj) throws Exception {

    }

    @Override
    public void returnObject(T obj) throws Exception {

    }
}
