package com.codedbykay.purenotes.utils

import androidx.compose.ui.unit.DpSize

fun DpSize.isBiggerOrEqualTo(other: DpSize): Boolean {
    return this.width >= other.width && this.height >= other.height
}
