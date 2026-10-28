package com.example

import com.example.data.FarmRepository
import com.example.model.CartItem
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testWeightAndPriceCalculation() {
        val repo = FarmRepository()
        val product = repo.products.value.first { it.id == "prod_1" }

        val item = CartItem(
            product = product,
            weight = 5.0,
            packageWeight = 2.5,
            numberOfPackages = 2,
            customUnitPrice = 2.80
        )

        assertEquals(14.0, item.totalPrice, 0.01)
        assertEquals(2, item.numberOfPackages)
    }

    @Test
    fun testTaxAndOrderPlacement() = runBlocking {
        val repo = FarmRepository()
        val product = repo.products.value.first { it.id == "prod_2" }

        val item = CartItem(
            product = product,
            weight = 2.0,
            packageWeight = 1.0,
            numberOfPackages = 2,
            customUnitPrice = 12.50
        )
        repo.addToCart(item)

        val initialStock = product.stockWeight

        val order = repo.placeOrder(
            buyerName = "Test Buyer",
            buyerContact = "555-0000",
            deliveryAddress = "123 Farm Way",
            paymentMethod = "Credit Card",
            fulfillmentType = "Delivery",
            coroutineScope = this
        )

        assertEquals(25.0, order.invoice.subtotal, 0.01)
        assertEquals(25.0 * 0.075, order.invoice.taxAmount, 0.01)
        assertEquals(4.99, order.invoice.deliveryFee, 0.01)
        assertEquals(25.0 + (25.0 * 0.075) + 4.99, order.invoice.totalAmount, 0.01)

        val updatedProduct = repo.products.value.first { it.id == "prod_2" }
        assertEquals(initialStock - 2.0, updatedProduct.stockWeight, 0.01)
    }

    @Test
    fun testRoleAssignment() {
        val consumerProfile = com.example.auth.UserProfile(
            uid = "user_123",
            email = "consumer@example.com",
            displayName = "Jane Doe",
            photoUrl = null,
            role = com.example.auth.UserRole.CONSUMER
        )
        assertEquals(com.example.auth.UserRole.CONSUMER, consumerProfile.role)
        assertEquals("Consumer / Buyer", consumerProfile.role.label)

        val growerProfile = consumerProfile.copy(role = com.example.auth.UserRole.GROWER)
        assertEquals(com.example.auth.UserRole.GROWER, growerProfile.role)
        assertEquals("Local Grower / Farmer", growerProfile.role.label)
    }
}
