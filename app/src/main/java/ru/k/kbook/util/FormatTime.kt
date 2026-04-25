package ru.k.kbook.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun Instant.showDate(): String {
    val formatter = DateTimeFormatter
        .ofPattern("HH:mm - dd.MM.yyyy")
        .withZone(ZoneOffset.UTC)
    return formatter.format(this)
}
