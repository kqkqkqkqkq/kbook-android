package ru.k.kbook.features.dish.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.k.kbook.R
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.Product

data class ProductWithQuantity(
    val product: Product,
    val quantity: Double,
)

@Composable
fun ProductDishCard(
    product: Product,
    quantity: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clickable(onClick = onClick),
    ) {
        val image = product.images.firstOrNull()
        AsyncImage(
            model = if (image?.contentType == ContentType.URL) image.url else image?.image,
            contentDescription = product.name,
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_launcher_background),
            error = painterResource(R.drawable.ic_launcher_background),
        )
        Column {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$quantity грамм",
                style = MaterialTheme.typography.bodyLarge,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
