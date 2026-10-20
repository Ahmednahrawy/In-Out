package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.model.Order

@Composable
fun CheckoutPaymentDialog(
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onConfirmOrder: (
        buyerName: String,
        buyerContact: String,
        deliveryAddress: String,
        paymentGateway: String,
        fulfillmentType: String
    ) -> Unit
) {
    var fulfillmentType by remember { mutableStateOf("Delivery") }
    var buyerName by remember { mutableStateOf("Sarah Jenkins") }
    var buyerContact by remember { mutableStateOf("(555) 234-5678") }
    var address by remember { mutableStateOf("742 Evergreen Terrace, Springfield") }

    // Payment Gateways
    var selectedGateway by remember { mutableStateOf("Mobile Wallet") } // Mobile Wallet, Credit Card, FarmPay Direct
    var cardNumber by remember { mutableStateOf("•••• •••• •••• 4242") }
    var cardExpiry by remember { mutableStateOf("08/28") }
    var cardCvv by remember { mutableStateOf("•••") }

    var isProcessing by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val taxAmount = subtotal * 0.075
    val deliveryFee = if (fulfillmentType == "Delivery") 4.99 else 0.0
    val totalAmount = subtotal + taxAmount + deliveryFee

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("checkout_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Secure Checkout",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Security & Encryption Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "End-to-End Encrypted Payment",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "Tokenized payment gateway. Customer data is strictly protected.",
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Fulfillment Selector
                Text(
                    text = "1. Fulfillment Method",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (fulfillmentType == "Delivery") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (fulfillmentType == "Delivery") BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { fulfillmentType = "Delivery" }
                            .padding(vertical = 4.dp)
                            .testTag("fulfillment_delivery")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Direct Delivery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("$4.99 (Same-Day)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (fulfillmentType == "Farm Pickup") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (fulfillmentType == "Farm Pickup") BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { fulfillmentType = "Farm Pickup" }
                            .padding(vertical = 4.dp)
                            .testTag("fulfillment_pickup")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Farm Stall Pickup", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Free (Scheduled)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Customer Fields
                OutlinedTextField(
                    value = buyerName,
                    onValueChange = { buyerName = it },
                    label = { Text("Your Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = if (fulfillmentType == "Delivery") address else "Highland Orchard Farm Stand, Ridge Crest Way",
                    onValueChange = { address = it },
                    label = { Text(if (fulfillmentType == "Delivery") "Delivery Address" else "Pickup Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = fulfillmentType == "Delivery"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Payment Gateway Selection
                Text(
                    text = "2. Payment Gateway",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val gateways = listOf(
                    Triple("Mobile Wallet", "Google Pay / Apple Pay", Icons.Default.PhoneAndroid),
                    Triple("Credit Card", "Visa / MasterCard / Amex", Icons.Default.CreditCard),
                    Triple("FarmPay Direct", "Zero-Fee Grower Transfer", Icons.Default.AccountBalance)
                )

                gateways.forEach { (id, desc, icon) ->
                    val isSelected = selectedGateway == id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedGateway = id }
                            .testTag("gateway_$id")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = id, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedGateway = id }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Price Breakdown & Automated Tax
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Items Subtotal (${cartItems.size} items):", fontSize = 12.sp)
                            Text("$${"%.2f".format(subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Agri Tax (7.5% Automated):", fontSize = 12.sp)
                            Text("$${"%.2f".format(taxAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fulfillment (${fulfillmentType}):", fontSize = 12.sp)
                            Text(if (deliveryFee > 0) "$${"%.2f".format(deliveryFee)}" else "FREE", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Charge:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "$${"%.2f".format(totalAmount)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Place Order Button
                Button(
                    onClick = {
                        isProcessing = true
                        onConfirmOrder(
                            buyerName,
                            buyerContact,
                            if (fulfillmentType == "Delivery") address else "Farm Pickup",
                            selectedGateway,
                            fulfillmentType
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_authorize_pay")
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Authorize & Pay $${"%.2f".format(totalAmount)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
