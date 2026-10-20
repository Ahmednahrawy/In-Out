package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FarmRepository
import com.example.model.*
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    repository: FarmRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // Observables from repository
    val farms by repository.farms.collectAsState()
    val products by repository.products.collectAsState()
    val cartItems by repository.cartItems.collectAsState()
    val orders by repository.orders.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()
    val reviews by repository.reviews.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val taxReports by repository.taxReports.collectAsState()

    // Local UI States
    var currentRole by remember { mutableStateOf(AppRole.BUYER) }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedFarm by remember { mutableStateOf<FarmLocation?>(null) }
    var maxDistanceMiles by remember { mutableStateOf(10f) }

    // Dialog & Modal Controllers
    var activeProductForConfig by remember { mutableStateOf<FarmProduct?>(null) }
    var activeInvoiceToShow by remember { mutableStateOf<Invoice?>(null) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showTrackingDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // Active order for tracking
    val latestOrder = orders.firstOrNull()

    // Filtered products for Buyer View
    val filteredProducts = products.filter { product ->
        val matchesCategory = selectedCategory == "All" || product.category.equals(selectedCategory, ignoreCase = true)
        val farm = farms.find { it.id == product.farmId }
        val matchesFarm = selectedFarm == null || product.farmId == selectedFarm?.id
        val matchesDistance = farm == null || farm.distanceMiles <= maxDistanceMiles
        matchesCategory && matchesFarm && matchesDistance
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Top App Bar with Branding & Quick Actions
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🍏 Rustic Orchard",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "Local Farm-to-Table Community",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        // AI Fresh Picks Button
                        IconButton(
                            onClick = { showAiDialog = true },
                            modifier = Modifier.testTag("action_gemini_ai")
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                                        Text("AI", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Gemini Recommendations",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Notifications Alert Button
                        IconButton(
                            onClick = { showNotificationsDialog = true },
                            modifier = Modifier.testTag("action_notifications")
                        ) {
                            val unreadCount = notifications.count { !it.isRead }
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge { Text("$unreadCount") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Alerts"
                                )
                            }
                        }

                        // Live Order Tracker Button (if active order exists)
                        if (orders.isNotEmpty()) {
                            IconButton(
                                onClick = { showTrackingDialog = true },
                                modifier = Modifier.testTag("action_track_order")
                            ) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Color(0xFF2E7D32)) {
                                            Text("${orders.size}")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = "Track Order",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Chat Button
                        IconButton(
                            onClick = { showChatDialog = true },
                            modifier = Modifier.testTag("action_chat")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Direct Chat"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // DUAL-ROLE TOGGLE SWITCH (Buyer vs Farmer / Admin)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("role_toggle_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Buyer Mode Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (currentRole == AppRole.BUYER) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { currentRole = AppRole.BUYER }
                                .padding(vertical = 2.dp)
                                .testTag("role_btn_buyer")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🍏 Buyer Mode",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRole == AppRole.BUYER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Farmer / Admin Mode Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (currentRole == AppRole.FARMER_ADMIN) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { currentRole = AppRole.FARMER_ADMIN }
                                .padding(vertical = 2.dp)
                                .testTag("role_btn_farmer")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🚜 Farmer / Admin",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRole == AppRole.FARMER_ADMIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Cart & Direct Invoicing Bottom Action Bar (shown when in Buyer mode with items in cart)
            if (currentRole == AppRole.BUYER && cartItems.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 10.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth().testTag("cart_bottom_bar")
                ) {
                    val subtotal = cartItems.sumOf { it.totalPrice }
                    val totalWeight = cartItems.sumOf { it.weight }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${cartItems.size} items (${"%.1f".format(totalWeight)} lbs)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$${"%.2f".format(subtotal)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Quick Produce Invoice Button
                            OutlinedButton(
                                onClick = {
                                    val farmName = cartItems.firstOrNull()?.product?.farmName ?: "Rustic Orchard Cooperative"
                                    val tax = subtotal * 0.075
                                    val inv = Invoice(
                                        invoiceNumber = "INV-${System.currentTimeMillis().toString().takeLast(6)}",
                                        issueDate = "Oct 19, 2026",
                                        buyerName = "Sarah Jenkins",
                                        buyerContact = "(555) 234-5678",
                                        farmName = farmName,
                                        items = cartItems,
                                        subtotal = subtotal,
                                        taxAmount = tax,
                                        deliveryFee = 4.99,
                                        totalAmount = subtotal + tax + 4.99,
                                        paymentMethod = "Authorized Card / Wallet",
                                        isPaid = true
                                    )
                                    activeInvoiceToShow = inv
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.height(44.dp).testTag("btn_produce_invoice_cart")
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Produce Invoice", fontSize = 12.sp)
                            }

                            // Checkout Button
                            Button(
                                onClick = { showCheckoutDialog = true },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                modifier = Modifier.height(44.dp).testTag("btn_open_checkout")
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Checkout", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRole) {
                AppRole.BUYER -> {
                    // BUYER VIEW: Split Map + Catalog
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("buyer_catalog_list"),
                        contentPadding = PaddingValues(bottom = if (cartItems.isNotEmpty()) 80.dp else 24.dp)
                    ) {
                        // 1. SPLIT VIEW: Interactive Map & Nearby Farm Cards
                        item {
                            SplitMapView(
                                farms = farms,
                                selectedFarm = selectedFarm,
                                onFarmSelected = { farm ->
                                    selectedFarm = if (selectedFarm?.id == farm.id) null else farm
                                },
                                maxDistanceMiles = maxDistanceMiles,
                                onDistanceChange = { maxDistanceMiles = it }
                            )
                        }

                        // Active Farm Filter Pill (if farm selected)
                        if (selectedFarm != null) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Filtering by: ${selectedFarm!!.name}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Show All Farms",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable { selectedFarm = null }
                                        )
                                    }
                                }
                            }
                        }

                        // 2. Category Filter Chips
                        item {
                            val categories = listOf("All", "Fruits", "Vegetables", "Dairy & Eggs", "Honey & Syrups")
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { category ->
                                    FilterChip(
                                        selected = selectedCategory == category,
                                        onClick = { selectedCategory = category },
                                        label = { Text(category, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        // Header: Available Farm Harvests
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Farm-Fresh Produce (${filteredProducts.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Prices set by farmers",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // 3. Products List with Custom Packaging & Weight Configuration Trigger
                        items(filteredProducts) { product ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 5.dp)
                                    .testTag("product_card_${product.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                            modifier = Modifier.size(52.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(text = product.emoji, fontSize = 28.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = product.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "$${"%.2f".format(product.pricePerUnit)} / ${product.unit}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(2.dp))

                                            Text(
                                                text = "${product.farmName} • ${product.farmLocation}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                if (product.isOrganicCertified) {
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = Color(0xFFE8F5E9)
                                                    ) {
                                                        Text(
                                                            text = "🌱 Organic",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF2E7D32),
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "Stock: ${"%.1f".format(product.stockWeight)} ${product.unit}",
                                                    fontSize = 11.sp,
                                                    color = if (product.stockWeight < 20) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "• ★ ${product.rating}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFFF4A261)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = product.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Action to open configuration dialog
                                    Button(
                                        onClick = { activeProductForConfig = product },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .testTag("btn_configure_${product.id}")
                                    ) {
                                        Icon(Icons.Default.Scale, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Select Weight & Configure Packages", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                AppRole.FARMER_ADMIN -> {
                    // FARMER / ADMIN VIEW: Inventory management, Turnover Analytics, Regional Tax Compliance
                    AdminDashboardView(
                        products = products,
                        taxReports = taxReports,
                        onUpdateStock = { id, stock, price, pkgW ->
                            repository.updateProductStock(id, stock, price, pkgW)
                        },
                        onAddNewProduct = { newProduct ->
                            repository.addNewProduct(newProduct)
                        }
                    )
                }
            }
        }
    }

    // MODAL DIALOGS

    // 1. Weight & Packaging Configurator Dialog
    if (activeProductForConfig != null) {
        ProductDetailWeightDialog(
            product = activeProductForConfig!!,
            onDismiss = { activeProductForConfig = null },
            onAddToCart = { item ->
                repository.addToCart(item)
            },
            onGenerateDirectInvoice = { item ->
                val tax = item.totalPrice * 0.075
                val inv = Invoice(
                    invoiceNumber = "INV-${System.currentTimeMillis().toString().takeLast(6)}",
                    issueDate = "Oct 19, 2026",
                    buyerName = "Sarah Jenkins",
                    buyerContact = "(555) 234-5678",
                    farmName = item.product.farmName,
                    items = listOf(item),
                    subtotal = item.totalPrice,
                    taxAmount = tax,
                    deliveryFee = 4.99,
                    totalAmount = item.totalPrice + tax + 4.99,
                    paymentMethod = "Mobile Wallet / Card (Direct)",
                    isPaid = true
                )
                activeInvoiceToShow = inv
            }
        )
    }

    // 2. Invoice Viewer & Share Sheet Dialog
    if (activeInvoiceToShow != null) {
        InvoiceDialog(
            invoice = activeInvoiceToShow!!,
            onDismiss = { activeInvoiceToShow = null }
        )
    }

    // 3. Multi-Gateway Checkout Dialog
    if (showCheckoutDialog) {
        CheckoutPaymentDialog(
            cartItems = cartItems,
            onDismiss = { showCheckoutDialog = false },
            onConfirmOrder = { buyerName, contact, address, gateway, fulfillment ->
                val placedOrder = repository.placeOrder(
                    buyerName = buyerName,
                    buyerContact = contact,
                    deliveryAddress = address,
                    paymentMethod = gateway,
                    fulfillmentType = fulfillment,
                    coroutineScope = coroutineScope
                )
                activeInvoiceToShow = placedOrder.invoice
            }
        )
    }

    // 4. Live Order & Logistics Tracker Dialog
    if (showTrackingDialog && latestOrder != null) {
        OrderTrackingDialog(
            order = latestOrder,
            onDismiss = { showTrackingDialog = false },
            onOpenChat = {
                showTrackingDialog = false
                showChatDialog = true
            },
            onViewInvoice = {
                showTrackingDialog = false
                activeInvoiceToShow = latestOrder.invoice
            },
            onOpenReview = {
                showTrackingDialog = false
                showReviewDialog = true
            }
        )
    }

    // 5. Vendor Direct Chat Dialog
    if (showChatDialog) {
        VendorChatDialog(
            messages = chatMessages,
            onSendMessage = { text, isFarmer ->
                repository.sendMessage(text, isFarmer, latestOrder?.id)
            },
            onDismiss = { showChatDialog = false }
        )
    }

    // 6. Farm Review & Transparency Dialog
    if (showReviewDialog) {
        FarmReviewDialog(
            reviews = reviews,
            farmName = selectedFarm?.name ?: "Highland Orchard & Apiary",
            farmId = selectedFarm?.id ?: "farm_2",
            onAddReview = { farmId, author, rating, text ->
                repository.addReview(farmId, author, rating, text)
            },
            onDismiss = { showReviewDialog = false }
        )
    }

    // 7. Gemini AI Recommendation Dialog
    if (showAiDialog) {
        AiRecommendationDialog(
            currentCartItems = cartItems,
            allProducts = products,
            onAddToCart = { item ->
                repository.addToCart(item)
            },
            onDismiss = { showAiDialog = false }
        )
    }

    // 8. Notifications Center Dialog
    if (showNotificationsDialog) {
        NotificationCenterDialog(
            notifications = notifications,
            onMarkAllRead = { repository.markNotificationsAsRead() },
            onDismiss = { showNotificationsDialog = false }
        )
    }
}
