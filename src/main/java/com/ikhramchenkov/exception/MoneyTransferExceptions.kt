package com.ikhramchenkov.exception

import javax.ws.rs.BadRequestException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

class AccountNotFoundException(accountNumber: String) : NotFoundException("Account $accountNumber is not found")

class AttemptToUseStrangersAccountException : ForbiddenException("Attempt to use stranger's account instead of own")

class InsufficientFundsException : ForbiddenException("Insufficient funds")

class SameAccountsInRequestException : BadRequestException("Don't need to transfer between same accounts")
