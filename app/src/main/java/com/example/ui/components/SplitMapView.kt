package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FarmLocation

@Composable
fun SplitMapView(
    farms: List<FarmLocation>,
    selectedFarm: FarmLocation?,
    onFarmSelected: (FarmLocation) -> Unit,
    maxDistanceMiles: Float,
    onDistanceChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("split_map_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Title and Radius Slider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Farm Map",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Local Farm Discovery Map",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Radius: ${maxDistanceMiles.toInt()} mi",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Radius Slider
            Slider(
                value = maxDistanceMiles,
                onValueChange = onDistanceChange,
                valueRange = 3f..25f,
                steps = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(32.dp)
                    .testTag("radius_slider")
            )

            // Top Half: Interactive Canvas Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE8EFEA))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(farms) {
                            detectTapGestures { tapOffset ->
                                val width = size.width
                                val height = size.height
                                val centerX = width / 2f
                                val centerY = height / 2f
                                val scale = minOf(width, height) * 0.40f

                                // Check which farm pin was tapped
                                val clickedFarm = farms.firstOrNull { farm ->
                                    val pinX = centerX + farm.lngOffset * scale
                                    val pinY = centerY + farm.latOffset * scale
                                    val distSq = (tapOffset.x - pinX) * (tapOffset.x - pinX) + (tapOffset.y - pinY) * (tapOffset.y - pinY)
                                    distSq <= 40f * 40f
                                }
                                if (clickedFarm != null) {
                                    onFarmSelected(clickedFarm)
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val centerX = width / 2f
                    val centerY = height / 2f
                    val scale = minOf(width, height) * 0.40f

                    // Draw subtle grid lines
                    val gridStep = 40f
                    for (x in 0..(width / gridStep).toInt()) {
                        drawLine(
                            color = Color(0xFFD6E2D9),
                            start = Offset(x * gridStep, 0f),
                            end = Offset(x * gridStep, height),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0..(height / gridStep).toInt()) {
                        drawLine(
                            color = Color(0xFFD6E2D9),
                            start = Offset(0f, y * gridStep),
                            end = Offset(width, y * gridStep),
                            strokeWidth = 1f
                        )
                    }

                    // Draw Radius Ring
                    val radiusRatio = (maxDistanceMiles / 25f).coerceIn(0.2f, 1f)
                    val radiusPx = scale * radiusRatio * 1.8f
                    drawCircle(
                        color = Color(0x202D6A4F),
                        center = Offset(centerX, centerY),
                        radius = radiusPx
                    )
                    drawCircle(
                        color = Color(0xFF2D6A4F),
                        center = Offset(centerX, centerY),
                        radius = radiusPx,
                        style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )

                    // Draw User Location Pin
                    drawCircle(
                        color = Color(0x401B4332),
                        center = Offset(centerX, centerY),
                        radius = 16f
                    )
                    drawCircle(
                        color = Color(0xFF1B4332),
                        center = Offset(centerX, centerY),
                        radius = 8f
                    )
                    drawCircle(
                        color = Color.White,
                        center = Offset(centerX, centerY),
                        radius = 3.5f
                    )

                    // Draw Farm Markers
                    farms.forEach { farm ->
                        val pinX = centerX + farm.lngOffset * scale
                        val pinY = centerY + farm.latOffset * scale
                        val isSelected = selectedFarm?.id == farm.id

                        // Pulsing outer halo if selected
                        if (isSelected) {
                            drawCircle(
                                color = Color(0x44D4A373),
                                center = Offset(pinX, pinY),
                                radius = 24f
                            )
                        }

                        // Pin Base
                        val pinColor = if (isSelected) Color(0xFFBC6C25) else Color(0xFF2D6A4F)
                        drawCircle(
                            color = pinColor,
                            center = Offset(pinX, pinY),
                            radius = if (isSelected) 14f else 11f
                        )
                        drawCircle(
                            color = Color.White,
                            center = Offset(pinX, pinY),
                            radius = if (isSelected) 5f else 4f
                        )
                    }
                }

                // Legend chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1B4332))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "You",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1B4332)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2D6A4F))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Farm Pin",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2D6A4F)
                        )
                    }
                }
            }

            // Bottom Split Half: Horizontal list of nearby farm cards
            Text(
                text = "Nearby Partner Farms (${farms.count { it.distanceMiles <= maxDistanceMiles }})",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 6.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(farms) { farm ->
                    val isSelected = selectedFarm?.id == farm.id
                    val isWithinRadius = farm.distanceMiles <= maxDistanceMiles

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        },
                        border = if (isSelected) {
                            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                        } else null,
                        modifier = Modifier
                            .width(220.dp)
                            .clickable { onFarmSelected(farm) }
                            .testTag("farm_card_${farm.id}")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = farm.emoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = farm.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        modifier = Modifier.width(120.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isWithinRadius) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                ) {
                                    Text(
                                        text = "${farm.distanceMiles} mi",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isWithinRadius) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Specialties: ${farm.specialties.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = Color(0xFFF4A261),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${farm.rating} (${farm.reviewCount} reviews)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "Pickup Ready",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
