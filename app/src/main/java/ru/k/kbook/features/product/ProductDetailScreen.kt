package ru.k.kbook.features.product

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.k.kbook.api.grpc.schema.Product

@Composable
fun ProductDetailScreen(
    id: Long,
    onNavigateBack: () -> Unit,
) {
    Button(
        onClick = onNavigateBack
    ) {
        Text(id.toString())
    }
}
