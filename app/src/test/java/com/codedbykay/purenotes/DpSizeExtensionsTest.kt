package com.codedbykay.purenotes

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.codedbykay.purenotes.utils.isBiggerOrEqualTo
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class DpSizeExtensionsTest {
    @Test
    fun `isBiggerOrEqualTo returns true when sizes are equal`() {
        val size1 = DpSize(Dp(100f), Dp(200f))
        val size2 = DpSize(Dp(100f), Dp(200f))

        assertTrue(size1.isBiggerOrEqualTo(size2))
    }

    @Test
    fun `isBiggerOrEqualTo returns true when this size is bigger`() {
        val size1 = DpSize(Dp(150f), Dp(250f))
        val size2 = DpSize(Dp(100f), Dp(200f))

        assertTrue(size1.isBiggerOrEqualTo(size2))
    }

    @Test
    fun `isBiggerOrEqualTo returns false when this size is smaller`() {
        val size1 = DpSize(Dp(50f), Dp(150f))
        val size2 = DpSize(Dp(100f), Dp(200f))

        assertFalse(size1.isBiggerOrEqualTo(size2))
    }
}