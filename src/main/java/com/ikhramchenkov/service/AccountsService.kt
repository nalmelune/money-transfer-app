package com.ikhramchenkov.service

import com.google.inject.Inject
import com.ikhramchenkov.dao.AccountsDao
import com.ikhramchenkov.entity.AccountEntity
import io.dropwizard.hibernate.UnitOfWork
import org.jvnet.hk2.annotations.Service

@Service
class AccountsService {

    @Inject
    private lateinit var accountsDao: AccountsDao

    @UnitOfWork
    fun findById(id: Long): AccountEntity? = accountsDao.findById(id)

}