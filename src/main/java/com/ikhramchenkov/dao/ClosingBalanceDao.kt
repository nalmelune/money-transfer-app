package com.ikhramchenkov.dao

import com.google.inject.Inject
import com.google.inject.Singleton
import com.ikhramchenkov.entity.ClosingBalance
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import java.time.LocalDate
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Root
import javax.persistence.criteria.Subquery

@Singleton
class ClosingBalanceDao @Inject constructor(sessionFactory: SessionFactory) :
    AbstractDAO<ClosingBalance>(sessionFactory) {

    fun findByAccountNumberAndLatestDate(accountNumber: String): ClosingBalance? {
        val query = criteriaQuery()

        query.from(entityClass).let { root ->
            val subQuery = query.subquery(LocalDate::class.java)
                .select(root.greatestPublishDate()).where(root.accountNumber(accountNumber))
            query.select(root).where(root.accountNumberAndGreatestPublishDate(accountNumber, subQuery))
        }
        return currentSession().createQuery(query).singleResult
    }

    private fun <T> Root<T>.greatestPublishDate() =
        criteriaBuilder.greatest(get<LocalDate>("publishDate"))

    private fun <T> Root<T>.accountNumberAndGreatestPublishDate(accountNumber: String, subQuery: Subquery<LocalDate>) =
        criteriaBuilder.and(
            criteriaBuilder.equal(get<T>("accountNumber"), accountNumber),
            criteriaBuilder.equal(get<T>("publishDate"), subQuery)
        )

    private fun <T> Root<T>.accountNumber(accountNumber: String) =
        criteriaBuilder.equal(get<T>("accountNumber"), accountNumber)


    private val criteriaBuilder: CriteriaBuilder
        get() = currentSession().criteriaBuilder
}


