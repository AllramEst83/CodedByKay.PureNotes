package com.codedbykay.purenotes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.codedbykay.purenotes.R

val AppFontFamily = FontFamily(
    Font(R.font.varela_round_regular, FontWeight.Normal)
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = AppFontFamily),
    displayMedium = TextStyle(fontFamily = AppFontFamily),
    displaySmall = TextStyle(fontFamily = AppFontFamily),
    headlineLarge = TextStyle(fontFamily = AppFontFamily),
    headlineMedium = TextStyle(fontFamily = AppFontFamily),
    headlineSmall = TextStyle(fontFamily = AppFontFamily),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 26.sp,
        fontWeight = FontWeight.Normal
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 22.sp,
        fontWeight = FontWeight.Normal
    ),
    titleSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyLarge = TextStyle(fontFamily = AppFontFamily),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(fontFamily = AppFontFamily),
    labelLarge = TextStyle(fontFamily = AppFontFamily),
    labelMedium = TextStyle(fontFamily = AppFontFamily),
    labelSmall = TextStyle(fontFamily = AppFontFamily)
)