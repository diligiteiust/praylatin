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

package org.latinpray.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownPadding
import org.latinpray.data.privacy
import org.latinpray.data.terms

@Composable
fun Legal(modifier: Modifier) {
    val legal by remember {
        mutableStateOf(
            terms.replace('\n', ' ') +" | " + privacy.replace('\n', ' ')
        )
    }
        Box(
            modifier = modifier
                .padding(vertical = 4.dp, horizontal = 16.dp),
        ) {
            Markdown(
                content = legal,
                padding = markdownPadding(
                    block = 4.dp,
                    //list = 0.dp,
                ),
                colors = markdownColor(
                    text = MaterialTheme.colorScheme.onBackground,
                    //linkText = MaterialTheme.colorScheme.onTertiary,
                ),
                typography = markdownTypography(
                    text = MaterialTheme.typography.bodySmall,
                    paragraph = MaterialTheme.typography.bodyMedium,
                    quote = MaterialTheme.typography.bodySmall,
                    h2 = MaterialTheme.typography.titleMedium,
                    h3 = MaterialTheme.typography.titleSmall,
                    //link = MaterialTheme.typography.labelMedium
                ),
                modifier = modifier
                    .background(color = MaterialTheme.colorScheme.background),
            )
        }
}