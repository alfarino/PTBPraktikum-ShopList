package com.example.shopinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import com.example.shopinglist.R

private data class ProfileInfo(
	val label: String,
	val value: String
)

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
	val profileItems = listOf(
		ProfileInfo("Nama", "Alfa Rino Svedrilio"),
		ProfileInfo("NIM", "2311522005"),
		ProfileInfo("Hobi", "Membaca"),
		ProfileInfo("Tempat, Tanggal Lahir", "Payakumbuh, 13 Agustus 2005"),
		ProfileInfo("Peminatan", "Mobile Programming")
	)

	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
		contentPadding = PaddingValues(bottom = 32.dp)
	) {
		item {
			Card(
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
			) {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(12.dp)
				) {
					Image(
						painter = painterResource(id = R.drawable.profile_picture),
						contentDescription = "Foto pribadi",
						modifier = Modifier
							.fillMaxWidth()
							.height(180.dp)
							.background(MaterialTheme.colorScheme.surfaceVariant),
						contentScale = ContentScale.Crop
					)
					Text(
						text = "Halo!",
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold
					)
					Text(
						text = "Saya mahasiswa yang tertarik untuk mengembangkan aplikasi mobile yang membantu aktivitas harian.",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
				}
			}
		}

		items(profileItems) { info ->
			Card(
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surfaceVariant
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(modifier = Modifier.padding(16.dp)) {
					Text(
						text = info.label,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.SemiBold
					)
					Spacer(modifier = Modifier.height(4.dp))
					Text(
						text = info.value,
						style = MaterialTheme.typography.bodyLarge
					)
				}
			}
		}

		item {
			HorizontalDivider()
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = "Senang bisa mengenalmu!",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

