package com.ikhramchenkov.entity

import javax.persistence.*

import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "balance_movements")
class BalanceMovement(

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private val id: Long? = null,

    @Column(name = "account_number_from")
    private val accountNumberFrom: Long? = null,

    @Column(name = "account_number_to")
    private val accountNumberTo: Long? = null,

    @Column(name = "amount")
    private val amount: Long? = null

)

