package ru.evgenykuzakov.rik_tt.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge =  TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.14).sp
    ),
    displayMedium =  TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.11).sp
    ),
    bodyMedium =  TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 15.sp,
        lineHeight = (15 * 1.16).sp,
        letterSpacing = 0.sp
    ),
    bodySmall =  TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.04).sp
    ),
    labelSmall = TextStyle(
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 10.sp,
        lineHeight = 11.sp,
        letterSpacing = ( -10 * 0.0011 ).sp
    ),
)