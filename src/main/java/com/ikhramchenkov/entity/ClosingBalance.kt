package com.ikhramchenkov.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(
    name = "closing_balance",
    indexes = [Index(columnList = "account_number,publish_date")]
)
class ClosingBalance(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,

    @Column(name = "account_number")
    var accountNumber: String? = null,

    @Column(name = "publish_date")
    var publishDate: LocalDate? = null,

    @Column(name = "end_of_period_balance")
    var endOfPeriodBalance: Long? = null,

    @Column(name = "summary_period_debit")
    var summaryPeriodDebit: Long? = null,

    @Column(name = "summary_period_credit")
    var summaryPeriodCredit: Long? = null

)
