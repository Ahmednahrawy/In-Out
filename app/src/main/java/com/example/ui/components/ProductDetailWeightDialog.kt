package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CartItem
import com.example.model.FarmProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailWeightDialog(
    product: FarmProduct,
    onDismiss: () -> Unit,
    onAddToCart: (CartItem) -> Unit,
    onGenerateDirectInvoice: (CartItem) -> Unit
) {
    // State for inputs
    var weightInput by remember { mutableStateOf(product.defaultPackageWeight.toString()) }
    var priceInput by remember { mutableStateOf(product.pricePerUnit.toString()) }
    var packageWeightInput by remember { mutableStateOf(product.defaultPackageWeight.toString()) }
    var packageCountInput by remember { mutableStateOf("1") }

    val currentWeight = weightInput.toDoubleOrNull() ?: 1.0
    val currentPrice = priceInput.toDoubleOrNull() ?: product.pricePerUnit
    val currentPackageWeight = packageWeightInput.toDoubleOrNull() ?: 1.0
    val currentPackageCount = packageCountInput.toIntOrNull() ?: 1

    val calculatedTotal = currentWeight * currentPrice

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("product_weight_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header: Emoji & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = product.emoji, fontSize = 24.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = product.farmName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Badges: Organic & In-Stock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (product.isOrganicCertified) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "🌱 USDA Certified Organic",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Stock: ${product.stockWeight} ${product.unit}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                Text(
                    text = "Package & Weight Customization",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input 1: Insert Weight
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = {
                        weightInput = it
                        val w = it.toDoubleOrNull() ?: 0.0
                        val pw = packageWeightInput.toDoubleOrNull() ?: 1.0
                        if (pw > 0) {
                            val count = (w / pw).toInt().coerceAtLeast(1)
                            packageCountInput = count.toString()
                        }
                    },
                    label = { Text("Total Weight (${product.unit})") },
                    leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) },
                    trailingIcon = {
                        Row(modifier = Modifier.padding(end = 4.dp)) {
                            FilledTonalIconButton(
                                onClick = {
                                    val nextW = (currentWeight - 0.5).coerceAtLeast(0.5)
                                    weightInput = "%.1f".format(nextW)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            FilledTonalIconButton(
                                onClick = {
                                    val nextW = (currentWeight + 0.5).coerceAtMost(product.stockWeight)
                                    weightInput = "%.1f".format(nextW)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_weight")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input 2: Insert Price (allows adjustment/custom unit pricing)
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Unit Price ($ per ${product.unit})") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_price")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input 3: Package Weight & Number of Packages
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = packageWeightInput,
                        onValueChange = {
                            packageWeightInput = it
                            val pw = it.toDoubleOrNull() ?: 1.0
                            val w = weightInput.toDoubleOrNull() ?: 1.0
                            if (pw > 0) {
                                packageCountInput = (w / pw).toInt().coerceAtLeast(1).toString()
                            }
                        },
                        label = { Text("Pkg Weight (${product.unit})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_package_weight")
                    )

                    OutlinedTextField(
                        value = packageCountInput,
                        onValueChange = {
                            packageCountInput = it
                            val count = it.toIntOrNull() ?: 1
                            val pw = packageWeightInput.toDoubleOrNull() ?: 1.0
                            weightInput = "%.1f".format(count * pw)
                        },
                        label = { Text("# of Packages") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_package_count")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Summary Calculation Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Packaging Specs:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$currentPackageCount boxes @ $currentPackageWeight ${product.unit}/box",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Item Subtotal:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${"%.2f".format(calculatedTotal)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Button(
                    onClick = {
                        val item = CartItem(
                            product = product,
                            weight = currentWeight,
                            packageWeight = currentPackageWeight,
                            numberOfPackages = currentPackageCount,
                            customUnitPrice = currentPrice
                        )
                        onAddToCart(item)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_add_to_list")
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add Product to List (${currentWeight} ${product.unit})")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        val item = CartItem(
                            product = product,
                            weight = currentWeight,
                            packageWeight = currentPackageWeight,
                            numberOfPackages = currentPackageCount,
                            customUnitPrice = currentPrice
                        )
                        onGenerateDirectInvoice(item)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_produce_invoice_direct")
                ) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Produce & Share Invoice")
                }
            }
        }
    }
}
