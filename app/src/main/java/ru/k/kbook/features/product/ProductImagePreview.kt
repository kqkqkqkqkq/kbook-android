package ru.k.kbook.features.product

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.ProductImage

@Composable
fun ProductImagePreview(
    image: ProductImage,
) {
    when {
        image.contentType == ContentType.URL && !image.url.isNullOrBlank() -> {
            coil3.compose.AsyncImage(model = image.url, contentDescription = null)
        }

        image.contentType == ContentType.IMAGE && image.image != null -> {
            val bitmap =
                BitmapFactory.decodeByteArray(image.image, 0, image.image.size)?.asImageBitmap()
            if (bitmap != null) {
                Image(bitmap = bitmap, contentDescription = null)
            }
        }
    }
}
