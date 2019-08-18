package com.ikhramchenkov.entity

import com.ikhramchenkov.enumeration.TransactionType
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.EnumType.STRING

@Entity
@Table(
    name = "balance_movements",
    indexes = [
        Index(columnList = "account_number,transaction_type"),
        Index(columnList = "created_at")
    ]
)
class BalanceMovement(

    @Id
    var uuid: UUID? = null,

    @Column(name = "account_number")
    var accountNumber: Long? = null,

    @Column(name = "amount")
    var amount: Long? = null,

    @Column(name = "transaction_type")
    @Enumerated(STRING)
    var transactionType: TransactionType,

    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null

)
