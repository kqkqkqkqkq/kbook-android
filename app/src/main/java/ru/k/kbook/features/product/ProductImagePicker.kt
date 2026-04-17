package ru.k.kbook.features.product

import android.content.Context
import android.net.Uri
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.ProductImage

fun contextUriToProductImage(context: Context, uri: Uri): ProductImage? {
    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
    return ProductImage(
        id = 0L,
        url = null,
        image = bytes,
        contentType = ContentType.IMAGE,
    )
}
