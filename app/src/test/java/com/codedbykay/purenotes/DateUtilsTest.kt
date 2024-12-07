package com.codedbykay.purenotes

import com.codedbykay.purenotes.utils.formatToString
import com.codedbykay.purenotes.utils.isFutureDate
import com.codedbykay.purenotes.utils.isPastDate
import com.codedbykay.purenotes.utils.toFormattedDate
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.util.Date
import java.util.TimeZone

class DateUtilsTest {
    private val utcTimeZone = TimeZone.getTimeZone("UTC")

    @Test
    fun `formatToString formats date correctly with default pattern`() {
        val date = Date(1672531200000L) // Equivalent to 2023-01-01 00:00 UTC
        val formattedDate = date.formatToString(timeZone = utcTimeZone)
        assertEquals("2023-01-01 00:00", formattedDate)
    }

    @Test
    fun `formatToString formats date correctly with custom pattern`() {
        val date = Date(1672531200000L)
        val formattedDate = date.formatToString(pattern = "dd/MM/yyyy", timeZone = utcTimeZone)
        assertEquals("01/01/2023", formattedDate)
    }

    @Test
    fun `toFormattedDate formats timestamp correctly with default pattern`() {
        val timestamp = 1672531200000L
        val formattedDate = timestamp.toFormattedDate(timeZone = utcTimeZone)
        assertEquals("2023-01-01 00:00", formattedDate)
    }

    @Test
    fun `toFormattedDate formats timestamp correctly with custom pattern`() {
        val timestamp = 1672531200000L
        val formattedDate =
            timestamp.toFormattedDate(pattern = "dd MMM yyyy", timeZone = utcTimeZone)
        assertEquals("01 Jan 2023", formattedDate)
    }

    @Test
    fun `isFutureDate returns true for future timestamp`() {
        val futureTimestamp = System.currentTimeMillis() + 10000L // 10 seconds in the future
        assertTrue(futureTimestamp.isFutureDate())
    }

    @Test
    fun `isFutureDate returns false for past timestamp`() {
        val pastTimestamp = System.currentTimeMillis() - 10000L // 10 seconds in the past
        assertFalse(pastTimestamp.isFutureDate())
    }

    @Test
    fun `isPastDate returns true for past timestamp`() {
        val pastTimestamp = System.currentTimeMillis() - 10000L
        assertTrue(pastTimestamp.isPastDate())
    }

    @Test
    fun `isPastDate returns false for future timestamp`() {
        val futureTimestamp = System.currentTimeMillis() + 10000L
        assertFalse(futureTimestamp.isPastDate())
    }
}