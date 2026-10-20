package com.example.model

data class FarmProduct(
    val id: String,
    val name: String,
    val farmId: String,
    val farmName: String,
    val farmLocation: String,
    val category: String, // Fruits, Vegetables, Dairy & Eggs, Honey & Syrups, Artisan Bakery
    val pricePerUnit: Double,
    val unit: String = "lb", // lb, kg, bunch, jar, carton
    val stockWeight: Double, // Available inventory weight/units
    val defaultPackageWeight: Double = 1.0, // Weight per individual package
    val minOrderWeight: Double = 0.5,
    val isOrganicCertified: Boolean = true,
    val harvestDate: String,
    val description: String,
    val rating: Double = 4.8,
    val reviewCount: Int = 18,
    val emoji: String = "🍎"
)

data class CartItem(
    val product: FarmProduct,
    val weight: Double, // Total weight selected
    val packageWeight: Double, // Weight per package
    val numberOfPackages: Int, // Number of individual boxes/bags
    val customUnitPrice: Double // Price per unit
) {
    val totalPrice: Double
        get() = weight * customUnitPrice
}

data class Invoice(
    val invoiceNumber: String,
    val issueDate: String,
    val buyerName: String,
    val buyerContact: String,
    val farmName: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val taxRate: Double = 0.075, // 7.5% regional agricultural tax
    val taxAmount: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val isPaid: Boolean = true,
    val fulfillmentType: String = "Delivery" // Delivery or Farm Pickup
) {
    fun toShareableText(): String {
        val itemsText = items.joinToString("\n") {
            " • ${it.product.name}: ${"%.2f".format(it.weight)} ${it.product.unit} (${it.numberOfPackages} pkgs @ ${"%.2f".format(it.packageWeight)} ${it.product.unit}/pkg) = $${"%.2f".format(it.totalPrice)}"
        }
        return """
            ====================================
               RUSTIC ORCHARD - FARM INVOICE
            ====================================
            Invoice #: $invoiceNumber
            Date: $issueDate
            Farm: $farmName
            Customer: $buyerName
            Fulfillment: $fulfillmentType
            
            ITEMS:
            $itemsText
            
            ------------------------------------
            Subtotal:      $${"%.2f".format(subtotal)}
            Tax (7.5%):    $${"%.2f".format(taxAmount)}
            Delivery Fee:  $${"%.2f".format(deliveryFee)}
            TOTAL:         $${"%.2f".format(totalAmount)}
            
            Payment: $paymentMethod (SECURE / ENCRYPTED)
            Status: ${if (isPaid) "PAID IN FULL" else "PENDING"}
            
            Thank you for supporting your local sustainable grower!
            ====================================
        """.trimIndent()
    }
}

enum class OrderStatus(val label: String, val stepIndex: Int) {
    CONFIRMED("Order Confirmed", 0),
    PACKED("Harvested & Packed", 1),
    OUT_FOR_DELIVERY("Out for Delivery", 2),
    DELIVERED("Delivered / Ready for Pickup", 3)
}

data class Order(
    val id: String,
    val invoice: Invoice,
    val status: OrderStatus,
    val etaMinutes: Int,
    val trackingProgress: Float, // 0.0f to 1.0f
    val courierName: String = "Local Farmer Logistics",
    val courierVehicle: String = "Eco-Electric Farm Van #12",
    val deliveryAddress: String,
    val placedAtTimestamp: Long = System.currentTimeMillis()
)

data class FarmLocation(
    val id: String,
    val name: String,
    val ownerName: String,
    val address: String,
    val distanceMiles: Double,
    val rating: Double,
    val reviewCount: Int,
    val specialties: List<String>,
    val latOffset: Float, // -1.0 to 1.0 for canvas map
    val lngOffset: Float, // -1.0 to 1.0 for canvas map
    val pickupHours: String,
    val emoji: String = "🏡"
)

data class FarmReview(
    val id: String,
    val farmId: String,
    val authorName: String,
    val rating: Int,
    val date: String,
    val reviewText: String,
    val verifiedBuyer: Boolean = true
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val isFromFarmer: Boolean,
    val text: String,
    val timestamp: String,
    val orderId: String? = null
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    ORDER_STATUS,
    RESTOCK_ALERT,
    PROMOTIONAL
}

data class TaxComplianceReport(
    val reportId: String,
    val period: String,
    val grossSales: Double,
    val exemptAgriculturalSales: Double,
    val taxableSpecialtySales: Double,
    val totalTaxRemitted: Double,
    val filingStatus: String = "Compliant",
    val generatedDate: String
)
