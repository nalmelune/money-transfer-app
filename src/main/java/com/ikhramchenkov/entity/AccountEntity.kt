package com.ikhramchenkov.entity

import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Table(
    name = "accounts",
    indexes = [Index(columnList = "account_number"),
        Index(columnList = "owner_id")]
)
@Entity
class AccountEntity(

    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,

    @Column(name = "account_number", nullable = false)
    var accountNumber: String? = null,

    @Column(name = "owner_id", nullable = false)
    var ownerId: Long? = null,

    @Column(name = "account_type", nullable = false)
    var accountType: String? = null,

    @Column(name = "description")
    var description: String? = null
)

