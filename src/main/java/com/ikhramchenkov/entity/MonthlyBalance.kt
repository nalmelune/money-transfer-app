package com.ikhramchenkov.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "monthly_balance",
    indexes = [Index(columnList = "accountNumber")])
class MonthlyBalance(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,

    @Column(name = "account_number")
    var accountNumber: String? = null,

    @Column(name = "publish_date")
    var publishDate: LocalDate? = null


)
