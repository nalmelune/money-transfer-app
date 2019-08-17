package com.ikhramchenkov.config

import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import com.ikhramchenkov.ApplicationHibernateBundle
import com.ikhramchenkov.dao.AccountsDao
import org.hibernate.SessionFactory


class GuiceModule : Module {

    override fun configure(binder: Binder) = Unit

    @Provides
    fun provideAccountsDao(sessionFactory: SessionFactory) = AccountsDao(sessionFactory)

    @Provides
    fun provideSessionFactory(
        hibernateBundle: ApplicationHibernateBundle
    ): SessionFactory = hibernateBundle.sessionFactory

}
