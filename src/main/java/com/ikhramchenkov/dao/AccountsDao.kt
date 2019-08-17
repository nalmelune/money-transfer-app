package com.ikhramchenkov.dao

import com.ikhramchenkov.entity.AccountEntity
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import javax.inject.Singleton

@Singleton
class AccountsDao(sessionFactory: SessionFactory) : AbstractDAO<AccountEntity>(sessionFactory) {

    fun findById(id: Long): AccountEntity? = get(id)

}