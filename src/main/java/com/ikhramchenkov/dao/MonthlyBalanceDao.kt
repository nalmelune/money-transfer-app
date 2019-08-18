package com.ikhramchenkov.dao

import com.google.inject.Singleton
import com.ikhramchenkov.entity.MonthlyBalance
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import java.time.LocalDate
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Root

@Singleton
class MonthlyBalanceDao(sessionFactory: SessionFactory) : AbstractDAO<MonthlyBalance>(sessionFactory) {

    fun findByAccountNumberAndDate(accountNumber: String, dateFrom: LocalDate): MonthlyBalance? {
        val query = criteriaQuery()
        query.from(MonthlyBalance::class.java).let { root ->
            query.select(root).where(root.accountBalanceAndPublishDateEquals(accountNumber, dateFrom))
        }
        return currentSession().createQuery(query).singleResult
    }

    private fun <T> Root<T>.accountBalanceAndPublishDateEquals(accountNumber: String, publishDate: LocalDate) =
        criteriaBuilder.and(
            criteriaBuilder.equal(get<T>("accountNumber"), accountNumber),
            criteriaBuilder.equal(get<T>("publishDate"), publishDate)
        )

    private val criteriaBuilder: CriteriaBuilder
        get() = currentSession().criteriaBuilder
}


