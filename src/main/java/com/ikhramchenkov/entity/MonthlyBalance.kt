package com.ikhramchenkov.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(
    name = "monthly_balance",
    indexes = [Index(columnList = "account_number")]
)
class MonthlyBalance(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,

    @Column(name = "account_number")
    var accountNumber: String? = null,

    @Column(name = "publish_date")
    var publishDate: LocalDate? = null,

    var endOfTheMonthBalance: Long? = null,

    var summaryMonthDebit: Long? = null,

    var summaryMonthCredit: Long? = null

)
