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
    @Column(name = "operation_token")
    val operationToken: UUID? = null,

    @Column(name = "account_from")
    val accountFrom: String? = null,

    @Column(name = "account_to")
    val accountTo: String? = null,

    @Column(name = "amount")
    val amount: Long? = null,

    @Column(name = "owner_id")
    val ownerId: Long? = null
)
