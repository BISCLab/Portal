package com.bisc.portal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bisc.portal.R

val CabinFontFamily = FontFamily(
    Font(R.font.cabin_regular, FontWeight.Normal),
    Font(R.font.cabin_regular, FontWeight.Medium),
    Font(R.font.cabin_bold,    FontWeight.SemiBold),
    Font(R.font.cabin_bold,    FontWeight.Bold),
    Font(R.font.cabin_italic,  FontWeight.Normal,   FontStyle.Italic),
    Font(R.font.cabin_bold,    FontWeight.Bold,     FontStyle.Italic),
)

val PortalTypography = Typography(
    displayLarge  = TextStyle(fontFamily = CabinFontFamily, fontSize = 57.sp, fontWeight = FontWeight.Normal, lineHeight = 64.sp),
    displayMedium = TextStyle(fontFamily = CabinFontFamily, fontSize = 45.sp, fontWeight = FontWeight.Normal, lineHeight = 52.sp),
    displaySmall  = TextStyle(fontFamily = CabinFontFamily, fontSize = 36.sp, fontWeight = FontWeight.Normal, lineHeight = 44.sp),
    headlineLarge  = TextStyle(fontFamily = CabinFontFamily, fontSize = 32.sp, fontWeight = FontWeight.Bold,   lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = CabinFontFamily, fontSize = 28.sp, fontWeight = FontWeight.Bold,   lineHeight = 36.sp),
    headlineSmall  = TextStyle(fontFamily = CabinFontFamily, fontSize = 24.sp, fontWeight = FontWeight.Bold,   lineHeight = 32.sp),
    titleLarge   = TextStyle(fontFamily = CabinFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Bold,    lineHeight = 28.sp),
    titleMedium  = TextStyle(fontFamily = CabinFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
    titleSmall   = TextStyle(fontFamily = CabinFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp),
    bodyLarge    = TextStyle(fontFamily = CabinFontFamily, fontSize = 16.sp, fontWeight = FontWeight.Normal,  lineHeight = 24.sp),
    bodyMedium   = TextStyle(fontFamily = CabinFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Normal,  lineHeight = 20.sp),
    bodySmall    = TextStyle(fontFamily = CabinFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Normal,  lineHeight = 16.sp),
    labelLarge   = TextStyle(fontFamily = CabinFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp),
    labelMedium  = TextStyle(fontFamily = CabinFontFamily, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp),
    labelSmall   = TextStyle(fontFamily = CabinFontFamily, fontSize = 11.sp, fontWeight = FontWeight.Normal,  lineHeight = 16.sp, letterSpacing = 0.5.sp),
)
