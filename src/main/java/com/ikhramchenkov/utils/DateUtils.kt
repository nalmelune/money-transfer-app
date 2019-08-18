package com.ikhramchenkov.utils

import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.lastDayOfMonth

fun lastDateOfPreviousMonth(dateToLookPreviousMonthFrom: LocalDate): LocalDate =
    dateToLookPreviousMonthFrom.minusMonths(1).with(lastDayOfMonth())
