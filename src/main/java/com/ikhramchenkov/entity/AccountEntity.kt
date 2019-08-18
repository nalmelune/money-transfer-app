package com.ikhramchenkov.entity

import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Table(
    name = "accounts",
    indexes = [Index(columnList = "account_number")]
)
@Entity
class AccountEntity(

    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,

    @Column(name = "account_number")
    var accountNumber: Long? = null,

    @Column(name = "owner_id")
    var ownerId: Long? = null,

    @Column(name = "account_type")
    var accountType: String? = null

)

