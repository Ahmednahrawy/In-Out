package com.example.data

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class FarmRepository {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // 1. Farms
    private val initialFarms = listOf(
        FarmLocation(
            id = "farm_1",
            name = "Willow Creek Organic Farm",
            ownerName = "Eleanor Vance",
            address = "4820 Valley River Rd, Millville",
            distanceMiles = 2.4,
            rating = 4.9,
            reviewCount = 38,
            specialties = listOf("Heirloom Apples", "Fresh Kale", "Carrots"),
            latOffset = -0.35f,
            lngOffset = -0.25f,
            pickupHours = "Mon - Sat: 8:00 AM - 6:00 PM",
            emoji = "🌾"
        ),
        FarmLocation(
            id = "farm_2",
            name = "Highland Orchard & Apiary",
            ownerName = "Marcus & Sarah Sterling",
            address = "715 Ridge Crest Way, Orchard Hills",
            distanceMiles = 4.1,
            rating = 4.9,
            reviewCount = 52,
            specialties = listOf("Raw Wildflower Honey", "Honeycrisp Apples", "Apple Cider"),
            latOffset = 0.40f,
            lngOffset = -0.30f,
            pickupHours = "Tue - Sun: 9:00 AM - 5:00 PM",
            emoji = "🐝"
        ),
        FarmLocation(
            id = "farm_3",
            name = "Meadow Brook Dairy & Creamery",
            ownerName = "Jacob Miller",
            address = "1200 Pasture Lane, Green Valley",
            distanceMiles = 5.8,
            rating = 4.8,
            reviewCount = 29,
            specialties = listOf("Grass-Fed Whole Milk", "Farmstead Cheddar", "Pastured Eggs"),
            latOffset = -0.20f,
            lngOffset = 0.45f,
            pickupHours = "Daily: 7:00 AM - 4:00 PM",
            emoji = "🧀"
        ),
        FarmLocation(
            id = "farm_4",
            name = "Sunny Slope Berry Patch",
            ownerName = "Chloe Bennett",
            address = "890 Sunrise Trail, West Haven",
            distanceMiles = 7.2,
            rating = 4.7,
            reviewCount = 24,
            specialties = listOf("Organic Strawberries", "Blueberries", "Artisan Jam"),
            latOffset = 0.30f,
            lngOffset = 0.35f,
            pickupHours = "Wed - Sun: 10:00 AM - 6:00 PM",
            emoji = "🍓"
        )
    )

    private val _farms = MutableStateFlow(initialFarms)
    val farms: StateFlow<List<FarmLocation>> = _farms.asStateFlow()

    // 2. Products Catalog & Live Inventory
    private val initialProducts = listOf(
        FarmProduct(
            id = "prod_1",
            name = "Honeycrisp Crisp Apples",
            farmId = "farm_2",
            farmName = "Highland Orchard & Apiary",
            farmLocation = "Orchard Hills (4.1 mi)",
            category = "Fruits",
            pricePerUnit = 2.80,
            unit = "lb",
            stockWeight = 145.0,
            defaultPackageWeight = 3.0,
            minOrderWeight = 1.0,
            isOrganicCertified = true,
            harvestDate = "Harvested 2 days ago",
            description = "Crisp, sweet, and bursting with cider-like juice. Hand-picked at peak ripeness.",
            rating = 4.9,
            reviewCount = 31,
            emoji = "🍎"
        ),
        FarmProduct(
            id = "prod_2",
            name = "Raw Wildflower Honey",
            farmId = "farm_2",
            farmName = "Highland Orchard & Apiary",
            farmLocation = "Orchard Hills (4.1 mi)",
            category = "Honey & Syrups",
            pricePerUnit = 12.50,
            unit = "jar",
            stockWeight = 42.0,
            defaultPackageWeight = 1.0,
            minOrderWeight = 1.0,
            isOrganicCertified = true,
            harvestDate = "Bottled this week",
            description = "Unfiltered, unpasteurized clover and wildflower honey collected directly from estate beehives.",
            rating = 5.0,
            reviewCount = 28,
            emoji = "🍯"
        ),
        FarmProduct(
            id = "prod_3",
            name = "Heirloom Rainbow Tomatoes",
            farmId = "farm_1",
            farmName = "Willow Creek Organic Farm",
            farmLocation = "Millville (2.4 mi)",
            category = "Vegetables",
            pricePerUnit = 3.90,
            unit = "lb",
            stockWeight = 85.0,
            defaultPackageWeight = 2.0,
            minOrderWeight = 0.5,
            isOrganicCertified = true,
            harvestDate = "Harvested yesterday",
            description = "Vibrant Brandywine, Cherokee Purple, and Green Zebra varieties rich in deep garden flavor.",
            rating = 4.8,
            reviewCount = 22,
            emoji = "🍅"
        ),
        FarmProduct(
            id = "prod_4",
            name = "Organic Tuscan Lacinato Kale",
            farmId = "farm_1",
            farmName = "Willow Creek Organic Farm",
            farmLocation = "Millville (2.4 mi)",
            category = "Vegetables",
            pricePerUnit = 2.20,
            unit = "bunch",
            stockWeight = 60.0,
            defaultPackageWeight = 1.0,
            minOrderWeight = 1.0,
            isOrganicCertified = true,
            harvestDate = "Fresh morning harvest",
            description = "Dark green, nutrient-packed dinosaur kale grown in compost-enriched river soil.",
            rating = 4.7,
            reviewCount = 14,
            emoji = "🥬"
        ),
        FarmProduct(
            id = "prod_5",
            name = "Pasture-Raised Brown Eggs",
            farmId = "farm_3",
            farmName = "Meadow Brook Dairy & Creamery",
            farmLocation = "Green Valley (5.8 mi)",
            category = "Dairy & Eggs",
            pricePerUnit = 6.20,
            unit = "carton",
            stockWeight = 50.0,
            defaultPackageWeight = 1.0,
            minOrderWeight = 1.0,
            isOrganicCertified = true,
            harvestDate = "Gathered yesterday",
            description = "Grade A large brown eggs with rich golden yolks from pasture-roaming heritage hens.",
            rating = 4.9,
            reviewCount = 45,
            emoji = "🥚"
        ),
        FarmProduct(
            id = "prod_6",
            name = "A2 Whole Grass-Fed Milk",
            farmId = "farm_3",
            farmName = "Meadow Brook Dairy & Creamery",
            farmLocation = "Green Valley (5.8 mi)",
            category = "Dairy & Eggs",
            pricePerUnit = 5.50,
            unit = "gallon",
            stockWeight = 25.0,
            defaultPackageWeight = 1.0,
            minOrderWeight = 1.0,
            isOrganicCertified = true,
            harvestDate = "Bottled today",
            description = "Non-homogenized, low-temp vat pasteurized milk with cream top from grass-fed Jersey cows.",
            rating = 4.8,
            reviewCount = 19,
            emoji = "🥛"
        ),
        FarmProduct(
            id = "prod_7",
            name = "Organic Mountain Strawberries",
            farmId = "farm_4",
            farmName = "Sunny Slope Berry Patch",
            farmLocation = "West Haven (7.2 mi)",
            category = "Fruits",
            pricePerUnit = 4.80,
            unit = "lb",
            stockWeight = 38.0,
            defaultPackageWeight = 1.0,
            minOrderWeight = 1.0,
            isOrganicCertified = true,
            harvestDate = "Picked this morning",
            description = "Intensely sweet, sun-warmed strawberries grown without synthetic sprays or pesticides.",
            rating = 4.9,
            reviewCount = 33,
            emoji = "🍓"
        )
    )

    private val _products = MutableStateFlow(initialProducts)
    val products: StateFlow<List<FarmProduct>> = _products.asStateFlow()

    // 3. Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // 4. Invoices & Orders
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // 5. Chat Messages
    private val initialChat = listOf(
        ChatMessage(
            id = "msg_1",
            senderName = "Marcus (Highland Orchard)",
            isFromFarmer = true,
            text = "Hello! We just bottled a fresh batch of raw wildflower honey. Let me know if you need any custom packaging!",
            timestamp = "10:15 AM"
        ),
        ChatMessage(
            id = "msg_2",
            senderName = "You",
            isFromFarmer = false,
            text = "Thanks Marcus! Are the Honeycrisp apples firm enough for baking pies this weekend?",
            timestamp = "10:18 AM"
        ),
        ChatMessage(
            id = "msg_3",
            senderName = "Marcus (Highland Orchard)",
            isFromFarmer = true,
            text = "Absolutely! They were harvested just 2 days ago and hold their texture wonderfully in the oven.",
            timestamp = "10:20 AM"
        )
    )
    private val _chatMessages = MutableStateFlow(initialChat)
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // 6. Reviews
    private val initialReviews = listOf(
        FarmReview(
            id = "rev_1",
            farmId = "farm_2",
            authorName = "Claire D.",
            rating = 5,
            date = "Oct 15, 2026",
            reviewText = "Best Honeycrisp apples in the county! The packaging was sturdy and the fruit was flawless.",
            verifiedBuyer = true
        ),
        FarmReview(
            id = "rev_2",
            farmId = "farm_1",
            authorName = "David K.",
            rating = 5,
            date = "Oct 14, 2026",
            reviewText = "The heirloom tomatoes made the most incredible summer salad. Will buy weekly!",
            verifiedBuyer = true
        ),
        FarmReview(
            id = "rev_3",
            farmId = "farm_3",
            authorName = "Rachel S.",
            rating = 5,
            date = "Oct 12, 2026",
            reviewText = "True old-school farm milk with rich cream on top. Reminds me of childhood dairy visits.",
            verifiedBuyer = true
        )
    )
    private val _reviews = MutableStateFlow(initialReviews)
    val reviews: StateFlow<List<FarmReview>> = _reviews.asStateFlow()

    // 7. Push / In-App Notifications
    private val initialNotifications = listOf(
        AppNotification(
            id = "notif_1",
            title = "Fresh Harvest Restock 🚜",
            message = "Highland Orchard just updated their inventory with 145 lbs of fresh Honeycrisp Apples!",
            timeAgo = "15m ago",
            type = NotificationType.RESTOCK_ALERT
        ),
        AppNotification(
            id = "notif_2",
            title = "Weekend Harvest Discount 🌿",
            message = "Save 15% on all organic root vegetables from Willow Creek Farm for local pickup.",
            timeAgo = "2h ago",
            type = NotificationType.PROMOTIONAL
        ),
        AppNotification(
            id = "notif_3",
            title = "Eco-Delivery Available 🚚",
            message = "Same-day temperature-controlled farm delivery is active in your neighborhood today.",
            timeAgo = "5h ago",
            type = NotificationType.ORDER_STATUS
        )
    )
    private val _notifications = MutableStateFlow(initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // 8. Tax & Compliance Report
    private val _taxReports = MutableStateFlow(
        listOf(
            TaxComplianceReport(
                reportId = "REP-2026-Q3",
                period = "Q3 2026 (Jul - Sep)",
                grossSales = 18450.00,
                exemptAgriculturalSales = 14200.00,
                taxableSpecialtySales = 4250.00,
                totalTaxRemitted = 318.75,
                filingStatus = "Filed & Compliant",
                generatedDate = "Oct 01, 2026"
            ),
            TaxComplianceReport(
                reportId = "REP-2026-M10",
                period = "October 2026 (Month-to-Date)",
                grossSales = 6320.00,
                exemptAgriculturalSales = 4900.00,
                taxableSpecialtySales = 1420.00,
                totalTaxRemitted = 106.50,
                filingStatus = "Accruing (Compliant)",
                generatedDate = "Oct 19, 2026"
            )
        )
    )
    val taxReports: StateFlow<List<TaxComplianceReport>> = _taxReports.asStateFlow()

    // Cart Management
    fun addToCart(item: CartItem) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.product.id == item.product.id }
        if (existingIndex >= 0) {
            current[existingIndex] = item
        } else {
            current.add(item)
        }
        _cartItems.value = current
    }

    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Checkout & Order Placement with Live Inventory Reduction
    fun placeOrder(
        buyerName: String,
        buyerContact: String,
        deliveryAddress: String,
        paymentMethod: String,
        fulfillmentType: String,
        coroutineScope: CoroutineScope
    ): Order {
        val items = _cartItems.value
        val subtotal = items.sumOf { it.totalPrice }
        val taxAmount = subtotal * 0.075
        val deliveryFee = if (fulfillmentType == "Delivery") 4.99 else 0.0
        val totalAmount = subtotal + taxAmount + deliveryFee
        val invoiceNumber = "INV-${System.currentTimeMillis().toString().takeLast(6)}"

        val farmName = items.firstOrNull()?.product?.farmName ?: "Rustic Orchard Cooperative"

        val invoice = Invoice(
            invoiceNumber = invoiceNumber,
            issueDate = dateFormat.format(Date()),
            buyerName = buyerName,
            buyerContact = buyerContact,
            farmName = farmName,
            items = items,
            subtotal = subtotal,
            taxAmount = taxAmount,
            deliveryFee = deliveryFee,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            isPaid = true,
            fulfillmentType = fulfillmentType
        )

        // Real-time stock decrement
        val currentProducts = _products.value.map { product ->
            val cartMatch = items.find { it.product.id == product.id }
            if (cartMatch != null) {
                val newStock = (product.stockWeight - cartMatch.weight).coerceAtLeast(0.0)
                product.copy(stockWeight = newStock)
            } else {
                product
            }
        }
        _products.value = currentProducts

        val newOrder = Order(
            id = "ORD-${System.currentTimeMillis().toString().takeLast(5)}",
            invoice = invoice,
            status = OrderStatus.CONFIRMED,
            etaMinutes = if (fulfillmentType == "Delivery") 35 else 15,
            trackingProgress = 0.15f,
            deliveryAddress = deliveryAddress
        )

        _orders.value = listOf(newOrder) + _orders.value
        clearCart()

        // Add confirmation notification
        addNotification(
            title = "Order Confirmed: ${newOrder.id} 📦",
            message = "Your order from $farmName for $${"%.2f".format(totalAmount)} is confirmed and scheduled.",
            type = NotificationType.ORDER_STATUS
        )

        // Simulate active delivery updates in background
        simulateOrderProgress(newOrder.id, coroutineScope)

        return newOrder
    }

    private fun simulateOrderProgress(orderId: String, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            delay(12000)
            updateOrderStatus(orderId, OrderStatus.PACKED, 0.45f, 25)
            addNotification(
                title = "Produce Harvested & Packed 🥕",
                message = "The farmer has packed your fresh produce in eco-insulated crates.",
                type = NotificationType.ORDER_STATUS
            )

            delay(14000)
            updateOrderStatus(orderId, OrderStatus.OUT_FOR_DELIVERY, 0.80f, 10)
            addNotification(
                title = "Order Out For Delivery 🚐",
                message = "Local farm courier is nearby! Estimated arrival in 10 minutes.",
                type = NotificationType.ORDER_STATUS
            )

            delay(16000)
            updateOrderStatus(orderId, OrderStatus.DELIVERED, 1.0f, 0)
            addNotification(
                title = "Delivered / Ready for Pickup! 🍏",
                message = "Your farm goods have arrived safe and fresh. Enjoy local quality!",
                type = NotificationType.ORDER_STATUS
            )
        }
    }

    private fun updateOrderStatus(orderId: String, status: OrderStatus, progress: Float, eta: Int) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                order.copy(status = status, trackingProgress = progress, etaMinutes = eta)
            } else {
                order
            }
        }
    }

    // Farmer Inventory Admin Actions
    fun updateProductStock(productId: String, newStockWeight: Double, newPrice: Double, newPackageWeight: Double) {
        _products.value = _products.value.map { product ->
            if (product.id == productId) {
                product.copy(
                    stockWeight = newStockWeight,
                    pricePerUnit = newPrice,
                    defaultPackageWeight = newPackageWeight
                )
            } else {
                product
            }
        }

        addNotification(
            title = "Inventory Updated 📝",
            message = "Stock levels and package weights updated successfully for local buyers.",
            type = NotificationType.RESTOCK_ALERT
        )
    }

    fun addNewProduct(product: FarmProduct) {
        _products.value = listOf(product) + _products.value
        addNotification(
            title = "New Harvest Listed! 🌟",
            message = "${product.farmName} listed fresh ${product.name} (${product.stockWeight} ${product.unit} available).",
            type = NotificationType.RESTOCK_ALERT
        )
    }

    // Messaging
    fun sendMessage(text: String, isFromFarmer: Boolean, orderId: String? = null) {
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = if (isFromFarmer) "Farmer Vendor" else "You (Buyer)",
            isFromFarmer = isFromFarmer,
            text = text,
            timestamp = timeFormat.format(Date()),
            orderId = orderId
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }

    // Reviews
    fun addReview(farmId: String, authorName: String, rating: Int, text: String) {
        val newReview = FarmReview(
            id = "rev_${System.currentTimeMillis()}",
            farmId = farmId,
            authorName = authorName,
            rating = rating,
            date = dateFormat.format(Date()),
            reviewText = text,
            verifiedBuyer = true
        )
        _reviews.value = listOf(newReview) + _reviews.value
    }

    // Notifications
    fun addNotification(title: String, message: String, type: NotificationType) {
        val newNotif = AppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            timeAgo = "Just now",
            type = type,
            isRead = false
        )
        _notifications.value = listOf(newNotif) + _notifications.value
    }

    fun markNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}
