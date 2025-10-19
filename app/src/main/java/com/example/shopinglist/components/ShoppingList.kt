package com.example.shopinglist.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shopinglist.ui.theme.ShoppingListTheme

@Composable
fun ShoppingList(items: List<String>, onItemClick: (String) -> Unit = {}) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it }) { item ->
            ShoppingListItem(item = item, onCardClick = { onItemClick(item) }, modifier = Modifier)
        }
    }
}

@Composable
fun ShoppingListItem(item: String, onCardClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    var isSelected by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, animationSpec = tween(200))
    val contentColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface, animationSpec = tween(200))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val avatarColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer, animationSpec = tween(200))

            Card(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(containerColor = avatarColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = item.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Text(
                text = item,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            val iconScale by animateFloatAsState(targetValue = if (isSelected) 1.15f else 1f, animationSpec = spring(stiffness = 400f))
            val iconBgColor by animateColorAsState(targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), animationSpec = tween(200))

            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { isSelected = !isSelected },
                shape = RoundedCornerShape(10.dp),
                color = iconBgColor
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (isSelected) "Selected" else "Add to cart",
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(6.dp)
                        .scale(iconScale)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview() {
    ShoppingListTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ShoppingList(items = listOf("Susu Segar", "Roti Tawar", "Telur Ayam", "Apel Fuji", "Daging Sapi"))
        }
    }
}
