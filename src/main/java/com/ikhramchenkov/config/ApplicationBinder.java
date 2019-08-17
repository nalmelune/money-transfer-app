package com.ikhramchenkov.config;

import com.ikhramchenkov.dao.AccountsDao;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(AccountsDao.class).to(AccountsDao.class).in(Singleton.class);
    }
}
