package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.FarmProduct
import com.example.model.TaxComplianceReport

@Composable
fun AdminDashboardView(
    products: List<FarmProduct>,
    taxReports: List<TaxComplianceReport>,
    onUpdateStock: (productId: String, newStock: Double, newPrice: Double, newPkgWeight: Double) -> Unit,
    onAddNewProduct: (FarmProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeAdminTab by remember { mutableStateOf(0) } // 0: Inventory Management, 1: Sales Analytics, 2: Regional Tax Compliance
    var showAddHarvestDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<FarmProduct?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("admin_dashboard_view")
    ) {
        // Farmer Overview Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🚜", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Highland Orchard & Farm Portal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Producer ID: US-AGRI-4820 • Verified Grower",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Button(
                        onClick = { showAddHarvestDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp).testTag("btn_add_harvest")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Harvest", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Top Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val totalInventoryWeight = products.sumOf { it.stockWeight }
                    val activeProductCount = products.size

                    AdminMetricBox(
                        title = "Gross Revenue",
                        value = "$24,770",
                        subtitle = "+18% vs last month",
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricBox(
                        title = "Live Inventory",
                        value = "${totalInventoryWeight.toInt()} lbs",
                        subtitle = "$activeProductCount active crops",
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricBox(
                        title = "Tax Status",
                        value = "Compliant",
                        subtitle = "7.5% remitted",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs
        TabRow(
            selectedTabIndex = activeAdminTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = activeAdminTab == 0,
                onClick = { activeAdminTab = 0 },
                text = { Text("Inventory (${products.size})", fontSize = 12.sp) }
            )
            Tab(
                selected = activeAdminTab == 1,
                onClick = { activeAdminTab = 1 },
                text = { Text("Sales Analytics", fontSize = 12.sp) }
            )
            Tab(
                selected = activeAdminTab == 2,
                onClick = { activeAdminTab = 2 },
                text = { Text("Tax & Compliance", fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        when (activeAdminTab) {
            0 -> {
                // Real-time Inventory Management Tab
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(products) { product ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = product.emoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = product.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${product.category} • ${product.farmName}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    FilledTonalIconButton(
                                        onClick = { editingProduct = product },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = "Available Stock:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "${"%.1f".format(product.stockWeight)} ${product.unit}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (product.stockWeight < 20) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Column {
                                        Text(text = "Unit Price:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "$${"%.2f".format(product.pricePerUnit)} / ${product.unit}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column {
                                        Text(text = "Package Size:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = "${product.defaultPackageWeight} ${product.unit}/box",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick Stock Adjuster
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Quick Adjust:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            val newStock = (product.stockWeight - 10.0).coerceAtLeast(0.0)
                                            onUpdateStock(product.id, newStock, product.pricePerUnit, product.defaultPackageWeight)
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("-10", fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    OutlinedButton(
                                        onClick = {
                                            val newStock = product.stockWeight + 25.0
                                            onUpdateStock(product.id, newStock, product.pricePerUnit, product.defaultPackageWeight)
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("+25", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Sales & Inventory Turnover Analytics Tab
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "Top Selling Farm Products", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                listOf(
                                    "Honeycrisp Crisp Apples" to "380 lbs sold ($1,064.00)",
                                    "Raw Wildflower Honey" to "84 jars sold ($1,050.00)",
                                    "Pasture-Raised Brown Eggs" to "112 cartons sold ($694.40)",
                                    "Heirloom Rainbow Tomatoes" to "145 lbs sold ($565.50)"
                                ).forEach { (item, stat) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "• $item", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(text = stat, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "Customer Fulfillment Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Direct Home Delivery:", fontSize = 12.sp)
                                        Text("68% (Eco-Van Fleet)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Column {
                                        Text("Farm Stall Pickup:", fontSize = 12.sp)
                                        Text("32% (Zero packaging waste)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Regional Tax & Agricultural Compliance Tab
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(taxReports) { report ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = report.period, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text(text = "Report Ref: ${report.reportId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFE8F5E9)
                                    ) {
                                        Text(
                                            text = report.filingStatus,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Gross Agricultural Sales:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$${"%.2f".format(report.grossSales)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Exempt Raw Farm Commodities:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$${"%.2f".format(report.exemptAgriculturalSales)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Taxable Prepared / Specialty (7.5%):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$${"%.2f".format(report.taxableSpecialtySales)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Sales Tax Remitted:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("$${"%.2f".format(report.totalTaxRemitted)}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = {
                                        val exportText = """
                                            REGIONAL AGRICULTURAL TAX COMPLIANCE REPORT
                                            Report ID: ${report.reportId}
                                            Period: ${report.period}
                                            Gross Sales: $${"%.2f".format(report.grossSales)}
                                            Tax Remitted: $${"%.2f".format(report.totalTaxRemitted)}
                                            Filing Status: ${report.filingStatus}
                                            Certified By Rustic Orchard Platform
                                        """.trimIndent()
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, exportText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Export Tax Compliance Report"))
                                    },
                                    modifier = Modifier.fillMaxWidth().height(38.dp)
                                ) {
                                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Export Compliance Report", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Edit Product Stock & Price
    if (editingProduct != null) {
        val prod = editingProduct!!
        var editStock by remember { mutableStateOf(prod.stockWeight.toString()) }
        var editPrice by remember { mutableStateOf(prod.pricePerUnit.toString()) }
        var editPkgWeight by remember { mutableStateOf(prod.defaultPackageWeight.toString()) }

        Dialog(onDismissRequest = { editingProduct = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Edit ${prod.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editStock,
                        onValueChange = { editStock = it },
                        label = { Text("Stock Weight (${prod.unit})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editPrice,
                        onValueChange = { editPrice = it },
                        label = { Text("Price per ${prod.unit} ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editPkgWeight,
                        onValueChange = { editPkgWeight = it },
                        label = { Text("Package Weight (${prod.unit})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { editingProduct = null }, modifier = Modifier.weight(1f)) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val s = editStock.toDoubleOrNull() ?: prod.stockWeight
                                val p = editPrice.toDoubleOrNull() ?: prod.pricePerUnit
                                val pw = editPkgWeight.toDoubleOrNull() ?: prod.defaultPackageWeight
                                onUpdateStock(prod.id, s, p, pw)
                                editingProduct = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    // Modal: Add New Harvest Product
    if (showAddHarvestDialog) {
        var newName by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Vegetables") }
        var newPrice by remember { mutableStateOf("3.50") }
        var newUnit by remember { mutableStateOf("lb") }
        var newStock by remember { mutableStateOf("50.0") }
        var newPackageWeight by remember { mutableStateOf("2.0") }
        var newEmoji by remember { mutableStateOf("🥦") }

        Dialog(onDismissRequest = { showAddHarvestDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "List New Farm Harvest", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Harvest Produce Name") },
                        placeholder = { Text("e.g. Organic Sugar Snap Peas") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newPrice,
                            onValueChange = { newPrice = it },
                            label = { Text("Price ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newUnit,
                            onValueChange = { newUnit = it },
                            label = { Text("Unit (lb, jar)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newStock,
                            onValueChange = { newStock = it },
                            label = { Text("Initial Stock") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newPackageWeight,
                            onValueChange = { newPackageWeight = it },
                            label = { Text("Pkg Weight") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showAddHarvestDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (newName.isNotBlank()) {
                                    val product = FarmProduct(
                                        id = "prod_${System.currentTimeMillis()}",
                                        name = newName.trim(),
                                        farmId = "farm_2",
                                        farmName = "Highland Orchard & Apiary",
                                        farmLocation = "Orchard Hills (4.1 mi)",
                                        category = newCategory,
                                        pricePerUnit = newPrice.toDoubleOrNull() ?: 3.50,
                                        unit = newUnit.trim(),
                                        stockWeight = newStock.toDoubleOrNull() ?: 50.0,
                                        defaultPackageWeight = newPackageWeight.toDoubleOrNull() ?: 2.0,
                                        harvestDate = "Just Harvested",
                                        description = "Freshly harvested and packaged directly by local orchard team.",
                                        emoji = newEmoji
                                    )
                                    onAddNewProduct(product)
                                    showAddHarvestDialog = false
                                }
                            },
                            enabled = newName.isNotBlank(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Publish Harvest")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
