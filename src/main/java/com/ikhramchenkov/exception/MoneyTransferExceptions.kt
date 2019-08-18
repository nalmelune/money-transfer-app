package com.ikhramchenkov.exception

import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

class AccountNotFound(accountNumber: String) : NotFoundException("Account $accountNumber is not found")

class AttemptToUseStrangersAccount : ForbiddenException("Attempt to use stranger's account instead of own")
