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
import androidx.compose.material3.ColorScheme
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

interface ThemeIfc {
    val darkColorTheme: ColorScheme
    val lightColorTheme: ColorScheme
}

object DefaultTheme : ThemeIfc {
    override val darkColorTheme: ColorScheme = darkColorScheme(
        background = OffBlack,
        onTertiary = Blue200,
    )
    override val lightColorTheme: ColorScheme = lightColorScheme(
        onTertiary = Blue900,
    )
}

object GoldenTheme: ThemeIfc {
    override val darkColorTheme: ColorScheme = darkColorScheme(
        background = OffBlack,
        onBackground = GoldenLightGrey,
        onTertiary = Blue200,
    )
    override val lightColorTheme: ColorScheme = lightColorScheme(
        onTertiary = Blue900,
        background = GoldenWhite,
        onBackground = GoldenDarkGrey,
        surfaceVariant = GoldenLightGrey,
    )
}

object AmberTheme: ThemeIfc {
    override val darkColorTheme: ColorScheme = darkColorScheme(
        background = AmberAn1BackBarely,
        onBackground = AmberAn1,
        onTertiary = Blue200,
    )
    override val lightColorTheme: ColorScheme = lightColorScheme(
        background = AmberAn1DarkSmoky,
        onTertiary = Blue900,
        onBackground = AmberAn1Soft2,
        surfaceVariant = GoldenLightGrey,
    )
}

object YellowTheme: ThemeIfc {
    override val darkColorTheme: ColorScheme = darkColorScheme(
        background = OffBlack,
        onBackground = LightYellowGray,
        onTertiary = Blue200,
    )
    override val lightColorTheme: ColorScheme = lightColorScheme(
        background = Gray50,
        onTertiary = Blue900,
        onBackground = DarkYellowGray,
    )
}

object BlueTheme: ThemeIfc {
    override val darkColorTheme: ColorScheme = darkColorScheme(
        background = OffBlack,
        onBackground = LightGreyBlue,
        onTertiary = Blue200,
    )
    override val lightColorTheme: ColorScheme = lightColorScheme(
        background = Gray50,
        onTertiary = Blue900,
        onBackground = DarkGreyBlue,
    )
}

object GreenTheme: ThemeIfc {
    override val darkColorTheme: ColorScheme = darkColorScheme(
        background = OffBlack,
        onBackground = LightGreyGreen,
        onTertiary = Blue200,
    )
    override val lightColorTheme: ColorScheme = lightColorScheme(
        background = Gray50,
        onTertiary = Blue900,
        onBackground = DarkGreyGreen,
    )
}

object RedTheme: ThemeIfc {
    override val darkColorTheme: ColorScheme = darkColorScheme(
        background = OffBlack,
        onBackground = LightRedGrey,
        onTertiary = Blue200,
    )
    override val lightColorTheme: ColorScheme = lightColorScheme(
        background = Gray50,
        onTertiary = Blue900,
        onBackground = DarkRedGrey,
    )
}

object OrangeTheme: ThemeIfc {
    override val darkColorTheme: ColorScheme = darkColorScheme(
        background = OffBlack,
        onBackground = LightGreyOrange,
        onTertiary = Orange200,
    )
    override val lightColorTheme: ColorScheme = lightColorScheme(
        background = OffWhite,
        onTertiary = Blue900,
        onBackground = DarkGreyOrange,
    )
}

val allThemes = mapOf(
    "Default" to DefaultTheme,
    "Golden" to GoldenTheme,
    "Amber" to AmberTheme,
    "Yellow" to YellowTheme,
    "Orange" to OrangeTheme,
    "Red" to RedTheme,
    "Blue" to BlueTheme,
    "Green" to GreenTheme,
)

@Composable
fun AppTheme(
    uiFontFactor: Float = 1.0f,
    headlineFontFactor: Float = 1.0f,
    contentFontFactor: Float = 1.0f,
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: String = "Default",
    content: @Composable () -> Unit
) {
    darkenBy = if (darkTheme) DARKEN_DARK_THEME_FACTOR else DARKEN_LIGHT_THEME_FACTOR
    val themeObj = allThemes[theme] ?: DefaultTheme
    val colors = if (darkTheme) {
        themeObj.darkColorTheme
    } else {
        themeObj.lightColorTheme
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
