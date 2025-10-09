package com.example.shopinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingScreen(modifier: Modifier = Modifier) {
	var notificationsEnabled by remember { mutableStateOf(true) }
	var darkThemeEnabled by remember { mutableStateOf(false) }

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Ini halaman Pengaturan",
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold
		)

		Card(
			colors = CardDefaults.cardColors(
				containerColor = MaterialTheme.colorScheme.surfaceVariant
			),
			modifier = Modifier.fillMaxWidth()
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				SettingToggleRow(
					title = "Notifikasi",
					description = "Aktifkan untuk menerima pengingat belanja",
					checked = notificationsEnabled,
					onCheckedChange = { notificationsEnabled = it }
				)
				SettingToggleRow(
					title = "Mode Gelap",
					description = "Sesuaikan tampilan dengan preferensi gelap",
					checked = darkThemeEnabled,
					onCheckedChange = { darkThemeEnabled = it }
				)
				Button(onClick = { /* TODO: tambahkan aksi simpan */ }) {
					Text(text = "Simpan Pengaturan")
				}
			}
		}
	}
}

@Composable
private fun SettingToggleRow(
	title: String,
	description: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit
) {
	Card(
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.background
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp)
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Medium
			)
			Text(
				text = description,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Switch(
				checked = checked,
				onCheckedChange = onCheckedChange
			)
		}
	}
}

