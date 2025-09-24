/*
 * This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. Look for COPYING file in the top folder.
 *  If not, see http://www.gnu.org/licenses/.
 */

package org.latinpray.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val TABLET_UI_FONT_FACTOR = 1.1f
const val TABLET_HEADLINE_FONT_FACTOR = 1.25f
const val TABLET_CONTENT_FONT_FACTOR = 1.5f

const val DARKEN_DARK_THEME_FACTOR = 0.8f
const val DARKEN_LIGHT_THEME_FACTOR = 0.9f
var darkenBy: Float = DARKEN_DARK_THEME_FACTOR

@Composable
fun AppTheme(
    uiFontFactor: Float = 1.0f,
    headlineFontFactor: Float = 1.0f,
    contentFontFactor: Float = 1.0f,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    darkenBy = if (darkTheme) DARKEN_DARK_THEME_FACTOR else DARKEN_LIGHT_THEME_FACTOR
    val colors = if (darkTheme) {
        darkColorScheme(
//            primary = Color(0xf0ebe0),
//            secondary = Color(0xdbceb4),
//            tertiary = Color(0xcbb893),
            background = Gray900,
            onBackground = Gray400,
//            surface = Color(0x312917),
//            onSurface = Color(0xf0ebe0),
//            onPrimary = Color(0x312917),
//            onSecondary = Color(0x312917),
//            onTertiary = Color(0x312917)
            onTertiary = Blue200,
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
            onTertiary = Blue900,
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
            textDecoration = TextDecoration.Underline,
            color = colors.onTertiary,
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
        content = content,
    )
}

fun Color.darken(): Color {
    return copy(
        red = red * darkenBy,
        green = green * darkenBy,
        blue = blue * darkenBy,
        alpha = alpha
    )
}
