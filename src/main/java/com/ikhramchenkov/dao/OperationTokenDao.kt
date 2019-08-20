package com.ikhramchenkov.dao

import com.google.inject.Inject
import com.google.inject.Singleton
import com.ikhramchenkov.entity.OperationToken
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import java.util.*

@Singleton
class OperationTokenDao @Inject constructor(sessionFactory: SessionFactory) :
    AbstractDAO<OperationToken>(sessionFactory) {

    fun save(operationToken: OperationToken): OperationToken? = persist(operationToken)

    fun findByToken(token: UUID): OperationToken? = get(token)

}
