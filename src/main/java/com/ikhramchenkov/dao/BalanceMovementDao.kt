package com.ikhramchenkov.dao

import com.google.inject.Singleton
import com.ikhramchenkov.entity.BalanceMovement
import com.ikhramchenkov.enumeration.TransactionType
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Root
import com.ikhramchenkov.enumeration.TransactionType.D as DEPOSIT
import com.ikhramchenkov.enumeration.TransactionType.W as WITHDRAWAL

@Singleton
class BalanceMovementDao(sessionFactory: SessionFactory) : AbstractDAO<BalanceMovement>(sessionFactory) {

    fun getDebitSince(accountNumber: String, dateSince: LocalDate): Long {
        return getByAccountNumberAndDateSinceAndTransactionType(accountNumber, dateSince, DEPOSIT)
    }

    fun getCreditSince(accountNumber: String, dateSince: LocalDate): Long {
        return getByAccountNumberAndDateSinceAndTransactionType(accountNumber, dateSince, WITHDRAWAL)
    }

    private fun getByAccountNumberAndDateSinceAndTransactionType(
        accountNumber: String,
        dateSince: LocalDate,
        type: TransactionType
    ): Long {
        val query = criteriaBuilder.createQuery(Long::class.java)

        query.from(entityClass).let { root ->
            query.select(criteriaBuilder.sum(root.get("amount")))
                .where(root.accountNumberEqualsAndDateAfterAndType(accountNumber, startOfNextDay(dateSince), type))
        }

        return currentSession().createQuery(query).singleResult
    }

    private fun <T> Root<T>.accountNumberEqualsAndDateAfterAndType(
        accountNumber: String,
        dateTimeSince: LocalDateTime,
        transactionType: TransactionType
    ) = criteriaBuilder.and(
        accountNumberEquals(accountNumber),
        criteriaBuilder.greaterThan(get<LocalDateTime>("created_at"), dateTimeSince),
        criteriaBuilder.equal(get<TransactionType>("transaction_type"), transactionType)
    )

    private fun <T> Root<T>.accountNumberEquals(
        accountNumber: String
    ) = criteriaBuilder.equal(get<T>("accountNumber"), accountNumber)

    private fun startOfNextDay(dateSince: LocalDate) = dateSince.plusDays(1).atStartOfDay()

    private val criteriaBuilder: CriteriaBuilder
        get() = currentSession().criteriaBuilder

}
