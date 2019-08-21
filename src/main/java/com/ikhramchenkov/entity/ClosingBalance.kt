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

    @Column(name = "account_number", nullable = false)
    var accountNumber: String? = null,

    @Column(name = "publish_date", nullable = false)
    var publishDate: LocalDate? = null,

    @Column(name = "end_of_period_balance", nullable = false)
    var endOfPeriodBalance: Long? = null,

    @Column(name = "summary_period_debit", nullable = false)
    var summaryPeriodDebit: Long? = null,

    @Column(name = "summary_period_credit", nullable = false)
    var summaryPeriodCredit: Long? = null

)
