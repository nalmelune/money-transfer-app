package com.ikhramchenkov.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "accounts")
@Entity
class AccountEntity(

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private val id: Long? = null,

    private val accountNumber: Long? = null,

    private val ownerId: Long? = null

)

