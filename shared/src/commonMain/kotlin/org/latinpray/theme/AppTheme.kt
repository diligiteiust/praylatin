package org.latinpray.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val TABLET_UI_FONT_FACTOR = 1.1f
const val TABLET_HEADLINE_FONT_FACTOR = 1.25f
const val TABLET_CONTENT_FONT_FACTOR = 1.5f

@Composable
fun AppTheme(
    uiFontFactor: Float = 1.0f,
    headlineFontFactor: Float = 1.0f,
    contentFontFactor: Float = 1.0f,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
//            primary = Color(0xf0ebe0),
//            secondary = Color(0xdbceb4),
//            tertiary = Color(0xcbb893),
//            background = Color(0x312917),
//            onBackground = Color(0xf0ebe0),
//            surface = Color(0x312917),
//            onSurface = Color(0xf0ebe0),
//            onPrimary = Color(0x312917),
//            onSecondary = Color(0x312917),
//            onTertiary = Color(0x312917)
        )
    } else {
        lightColorScheme(
//            primary = Color(0x312917),
//            onPrimary = Color(0xf0ebe0),
//
//            secondary = Color(0x473b22),
//            onSecondary = Color(0xf0ebe0),
//
//            tertiary = Color(0x211b0f),
//            onTertiary = Color(0xf0ebe0),
//
//            background = Color(0xf0ebe0),
//            onBackground = Color(0x312917),
//
//            surface = Color(0xf0ebe0),
//            onSurface = Color(0x312917),
//
//            error = Color(0xf0ebe0),
//            onError = Color(0x312917),
//            errorContainer = Color(0xf0ebe0),
//            onErrorContainer = Color(0x312917),
//            outline = Color(0x473b22),
//            outlineVariant = Color(0x473b22),
//            scrim = Color(0x00000000),
//            inverseSurface = Color(0xf0ebe0),
//            inverseOnSurface = Color(0x312917),
//            inversePrimary = Color(0xf0ebe0),
//            surfaceVariant = Color(0xdbceb4),
//            onSurfaceVariant = Color(0x473b22),
//            surfaceTint = Color(0x312917),
        )
    }
    val typography = Typography(
        bodySmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp * contentFontFactor
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp * contentFontFactor
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp * contentFontFactor
        ),
        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp * contentFontFactor,
        ),
        titleSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp * contentFontFactor,
        ),
        labelMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp * uiFontFactor,
            textDecoration = TextDecoration.Underline
        ),
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp * headlineFontFactor,
        ),
        headlineMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp * headlineFontFactor,
        ),
        headlineSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp * headlineFontFactor,
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
