package com.codedbykay.purenotes.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


/**
 * Extension functions for date formatting in Pure Notes app
 */

private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm"

/**
 * Formats a Date object to string using the specified pattern
 * @param pattern The date format pattern. Defaults to "yyyy-MM-dd HH:mm"
 * @param locale The locale to use for formatting. Defaults to Locale.ENGLISH
 * @return Formatted date string
 */
fun Date.formatToString(
    pattern: String = DEFAULT_DATE_FORMAT,
    locale: Locale = Locale.ENGLISH,
    timeZone: TimeZone = TimeZone.getDefault(),
): String {
    return SimpleDateFormat(pattern, locale).apply {
        this.timeZone = timeZone
    }.format(this)
}

/**
 * Formats a timestamp (milliseconds since epoch) to string
 * @param pattern The date format pattern. Defaults to "yyyy-MM-dd HH:mm"
 * @param locale The locale to use for formatting. Defaults to Locale.ENGLISH
 * @return Formatted date string
 */
fun Long.toFormattedDate(
    pattern: String = DEFAULT_DATE_FORMAT,
    locale: Locale = Locale.ENGLISH,
    timeZone: TimeZone = TimeZone.getDefault(),
): String {
    return SimpleDateFormat(pattern, locale).apply {
        this.timeZone = timeZone
    }.format(Date(this))
}

/**
 * Checks if a timestamp represents a valid future date
 * @return true if the timestamp is in the future
 */
fun Long.isFutureDate(): Boolean {
    return this > System.currentTimeMillis()
}

/**
 * Checks if a timestamp represents a valid past date
 * @return true if the timestamp is in the past
 */
fun Long.isPastDate(): Boolean {
    return this < System.currentTimeMillis()
}