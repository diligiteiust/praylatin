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

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownPadding
import com.revenuecat.purchases.kmp.Purchases
import kotlinx.coroutines.launch
import org.latinpray.data.Config
import org.latinpray.data.offers

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HelpScreen(
    title: String,
    content: String,
    goBack: () -> Unit,
    config: Config,
    //animatedContentScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val (fraction, setFraction) = remember { mutableStateOf(0.25f) }
    val scope = rememberCoroutineScope()

    if (sharedTransitionScope.isTransitionActive.not()) {
        setFraction(0f)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
//                .background(color = MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .windowInsetsPadding(WindowInsets.systemBars),
            //verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(50.dp)
                .align(Alignment.CenterStart)
                .padding(10.dp)
                .alpha(fraction)
                //.alpha(alpha = if (fraction <= 0) 1f else 0f)
                .background(
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(50)
                ).shadow(elevation = 16.dp).padding(5.dp).clickable {
                    goBack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(30.dp)
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Markdown(
                content = content,
                padding = markdownPadding(
                    block = 4.dp,
                    //list = 0.dp,
                ),
                colors = markdownColor(
                    text = MaterialTheme.colorScheme.onBackground,
                ),
                typography = markdownTypography(
                    text = MaterialTheme.typography.bodySmall,
                    paragraph = MaterialTheme.typography.bodyMedium,
                    quote = MaterialTheme.typography.bodySmall,
                    h2 = MaterialTheme.typography.titleMedium,
                    h3 = MaterialTheme.typography.titleSmall,
                    link = MaterialTheme.typography.labelMedium
                ),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
                    .background(color = MaterialTheme.colorScheme.background),
            )

        }
        if (offers == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Loading...")
            }
            //println("Offers are null")
        } else {
            var currentDonation by remember { mutableStateOf<String?>(config.donation) }
            offers!!.forEach { offer ->
                //println("Offer title: ${offer.storeProduct.title}, description: ${offer.storeProduct.id}, price: ${offer.storeProduct.price.formatted}")
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        enabled = offer.storeProduct.id != currentDonation,
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 16.dp
                        ),
                        onClick = {
                            Purchases.sharedInstance.purchase(
                                storeProduct = offer.storeProduct,
                                onError = { error, userCancelled ->
                                    // An error occurred
                                    println("Error: $error, cancelled: $userCancelled")
                                    //Text(text = "Error: $error")
                                },
                                onSuccess = { storeTransaction, customerInfo ->
                                    // Purchase was successful
                                    println("Success: $storeTransaction, $customerInfo")
                                    scope.launch {
                                        config.saveDonation(offer.storeProduct.id)
                                    }
                                    currentDonation = offer.storeProduct.id
                                }
                            )
                        }
                    )
                    {
                        val tit = if (offer.storeProduct.id == currentDonation) {
                            "Active"
                        } else {
                            "Monthly"
                        }
                        Text(text = "$tit donation of " + offer.storeProduct.price.formatted)
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(
                    onClick = {
                        Purchases.sharedInstance.restorePurchases(
                            onError = { error ->
                                // An error occurred
                                println("Error: $error")
                                //Text(text = "Error: $error")
                            },
                            onSuccess = { customerInfo ->
                                // Purchases were restored
                                println("Success: $customerInfo")
                                if (customerInfo.entitlements.active.isNotEmpty()) {
                                    customerInfo.entitlements.active.forEach {
                                        val product = it.value.productIdentifier
                                        offers?.find { offer ->
                                            offer.storeProduct.id == product
                                        }?.let { offer ->
                                            val subs = offer.storeProduct.title + " " + offer.storeProduct.price.formatted
                                            println("Active purchase: $subs")
                                            scope.launch {
                                                config.saveDonation(product)
                                            }
                                            currentDonation = product
                                        }
                                    }
                                } else {
                                    println("No active purchases")
                                }
                            }
                        )
                    }
                ) {
                    Text(text = "Restore donations")
                }
            }

        }
    }
}

