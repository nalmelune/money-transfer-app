package com.ikhramchenkov.dao

import com.google.inject.Singleton
import com.ikhramchenkov.entity.AccountEntity
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Root

@Singleton
class AccountsDao(sessionFactory: SessionFactory) : AbstractDAO<AccountEntity>(sessionFactory) {

    fun findById(id: Long): AccountEntity? = get(id)

    fun findByNumber(accountNumber: String): AccountEntity? {
        val query = criteriaQuery()
        query.from(AccountEntity::class.java).let { root ->
            query.select(root).where(root.accountNumberEquals(accountNumber))
        }
        return currentSession().createQuery(query).singleResult
    }

    private fun <T> Root<T>.accountNumberEquals(
        accountNumber: String
    ) = criteriaBuilder.equal(get<T>("accountNumber"), accountNumber)

    private val criteriaBuilder: CriteriaBuilder
        get() = currentSession().criteriaBuilder
}