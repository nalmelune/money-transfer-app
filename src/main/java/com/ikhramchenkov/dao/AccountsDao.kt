package com.ikhramchenkov.dao

import com.google.inject.Singleton
import com.ikhramchenkov.entity.AccountEntity
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.LockMode.PESSIMISTIC_WRITE
import org.hibernate.SessionFactory
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Root

@Singleton
class AccountsDao(sessionFactory: SessionFactory) : AbstractDAO<AccountEntity>(sessionFactory) {

    fun findByOwner(ownerId: Long): List<AccountEntity> {
        val query = criteriaQuery()
        query.from(entityClass).let { root ->
            query.select(root).where(root.ownerIdEquals(ownerId))
        }
        return currentSession().createQuery(query).resultList
    }

    fun findByNumber(accountNumber: String): AccountEntity? {
        val query = criteriaQuery()
        query.from(entityClass).let { root ->
            query.select(root).where(root.accountNumberEquals(accountNumber))
        }
        return currentSession().createQuery(query).singleResult
    }

    fun lock(accountEntity: AccountEntity) {
        currentSession().lock(accountEntity, PESSIMISTIC_WRITE)
    }

    private fun <T> Root<T>.ownerIdEquals(
        ownerId: Long
    ) = criteriaBuilder.equal(get<T>("ownerId"), ownerId)

    private fun <T> Root<T>.accountNumberEquals(
        accountNumber: String
    ) = criteriaBuilder.equal(get<T>("accountNumber"), accountNumber)

    private val criteriaBuilder: CriteriaBuilder
        get() = currentSession().criteriaBuilder
}