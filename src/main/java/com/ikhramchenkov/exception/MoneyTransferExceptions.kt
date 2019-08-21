package com.ikhramchenkov.exception

import javax.ws.rs.BadRequestException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

class AccountNotFoundException(accountNumber: String) : NotFoundException("Account $accountNumber is not found")

class AttemptToUseStrangersAccountException : ForbiddenException(REASON) {
    companion object {
        const val REASON = "Attempt to use stranger's account instead of own"
    }
}

class InsufficientFundsException : ForbiddenException(REASON) {
    companion object {
        const val REASON = "Insufficient funds"
    }
}

class SameAccountsInRequestException : BadRequestException(REASON) {
    companion object {
        const val REASON = "Don't need to transfer between same accounts"
    }
}

class NoSuchTokenException : NotFoundException(REASON) {
    companion object {
        const val REASON = "No token to confirm"
    }
}