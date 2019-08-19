package com.ikhramchenkov.entity

import com.ikhramchenkov.enumeration.TransactionType
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.persistence.GenerationType.IDENTITY

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
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,

    @Column(name = "account_number")
    var accountNumber: String? = null,

    @Column(name = "amount")
    var amount: Long? = null,

    @Column(name = "transaction_type")
    @Enumerated(STRING)
    var transactionType: TransactionType? = null,

    @Column(name = "operation_number")
    var operationNumber: UUID? = null,

    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null

)
