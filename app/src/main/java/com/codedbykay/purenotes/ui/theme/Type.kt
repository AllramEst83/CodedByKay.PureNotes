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

/**
 * Typography Usage Guide:
 *
 * 1. Display Styles:
 *    - displayLarge: Used for large and prominent text, such as hero banners or splash screens.
 *    - displayMedium: Ideal for slightly smaller yet impactful text, like section headers in full-screen layouts.
 *    - displaySmall: Suitable for large headings or subtitles in more constrained spaces, like cards or dialogs.
 *
 * 2. Headline Styles:
 *    - headlineLarge: Used for prominent headings, such as page titles.
 *    - headlineMedium: Ideal for subsection headings or larger titles in compact layouts.
 *    - headlineSmall: Suitable for secondary headings or smaller page titles.
 *
 * 3. Title Styles:
 *    - titleLarge: Used for key text in compact spaces, like app bar titles.
 *    - titleMedium: Suitable for medium emphasis text, like list item titles or dialog titles.
 *    - titleSmall: Ideal for less prominent titles or supporting text within list items.
 *
 * 4. Body Styles:
 *    - bodyLarge: Used for main content text, such as paragraphs or articles.
 *    - bodyMedium: Ideal for primary content text with slightly more emphasis, like subtitles.
 *    - bodySmall: Suitable for secondary or supplementary text, such as captions or disclaimers.
 *
 * 5. Label Styles:
 *    - labelLarge: Used for prominent labels, like buttons or form labels.
 *    - labelMedium: Suitable for medium emphasis labels, like supporting button text.
 *    - labelSmall: Ideal for small and subtle labels, like helper text or captions.
 *
 * Note:
 * Always choose typography styles based on the design's information hierarchy,
 * ensuring text remains readable and aligned with Material Design guidelines.
 */


val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 57.sp,
        fontWeight = FontWeight.Normal
    ),
    displayMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 45.sp,
        fontWeight = FontWeight.Normal
    ),
    displaySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 36.sp,
        fontWeight = FontWeight.Normal
    ),
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 28.sp,
        fontWeight = FontWeight.Normal
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal
    ),
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
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
    )
)
