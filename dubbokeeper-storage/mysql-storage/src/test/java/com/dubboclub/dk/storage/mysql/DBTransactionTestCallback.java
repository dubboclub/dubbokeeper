package com.dubboclub.dk.storage.mysql;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * @date: 2015/12/17.
 * @author:bieber.
 * @project:dubbokeeper.
 * @package:com.dubboclub.dk.storage.mysql.
 * @version:1.0.0
 * @fix:
 * @description: 描述功能
 */
public abstract class DBTransactionTestCallback<T extends Object> implements TransactionCallback<T> {
    @Override
    public T doInTransaction(TransactionStatus status) {
        T result =  doInTransaction();
        status.setRollbackOnly();
        return result;
    }

    protected abstract T doInTransaction();
}
