package com.ikhramchenkov.entity

import java.util.*
import javax.persistence.*

@Entity
@Table(
    name = "operation_tokens",
    indexes = [Index(columnList = "operation_token")]
)
class OperationToken(
    @Id
    @Column(name = "operation_token", nullable = false)
    val operationToken: UUID? = null,

    @Column(name = "account_from", nullable = false)
    val accountFrom: String? = null,

    @Column(name = "account_to", nullable = false)
    val accountTo: String? = null,

    @Column(name = "amount", nullable = false)
    val amount: Long? = null,

    @Column(name = "owner_id", nullable = false)
    val ownerId: Long? = null
)
