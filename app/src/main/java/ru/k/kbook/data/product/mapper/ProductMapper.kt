package ru.k.kbook.data.product.mapper

import com.google.protobuf.ByteString
import com.google.protobuf.kotlin.toByteString
import ru.k.kbook.api.grpc.request.CreateProductRequestDto
import ru.k.kbook.api.grpc.request.DeleteProductRequestDto
import ru.k.kbook.api.grpc.request.GetProductRequestDto
import ru.k.kbook.api.grpc.request.GetProductsForDishRequestDto
import ru.k.kbook.api.grpc.request.ListProductsRequestDto
import ru.k.kbook.api.grpc.request.UpdateProductRequestDto
import ru.k.kbook.api.grpc.response.DeleteProductResponseDto
import ru.k.kbook.api.grpc.response.ListProductsResponseDto
import ru.k.kbook.api.grpc.response.ProductResponseDto
import ru.k.kbook.api.grpc.schema.ContentType
import ru.k.kbook.api.grpc.schema.CookingRequired
import ru.k.kbook.api.grpc.schema.Product
import ru.k.kbook.api.grpc.schema.ProductCategory
import ru.k.kbook.api.grpc.schema.ProductFlag
import ru.k.kbook.api.grpc.schema.ProductImage
import ru.k.kbook.api.grpc.schema.SortDirection
import ru.k.kbook.api.grpc.schema.SortField
import ru.k.kbook_api.grpc.product.ContentTypeDto
import ru.k.kbook_api.grpc.product.CookingRequiredDto
import ru.k.kbook_api.grpc.product.CreateProductRequest
import ru.k.kbook_api.grpc.product.DeleteProductRequest
import ru.k.kbook_api.grpc.product.DeleteProductResponse
import ru.k.kbook_api.grpc.product.GetProductRequest
import ru.k.kbook_api.grpc.product.GetProductsForDishRequest
import ru.k.kbook_api.grpc.product.ListProductsRequest
import ru.k.kbook_api.grpc.product.ListProductsResponse
import ru.k.kbook_api.grpc.product.ProductCategoryDto
import ru.k.kbook_api.grpc.product.ProductDto
import ru.k.kbook_api.grpc.product.ProductFlagDto
import ru.k.kbook_api.grpc.product.ProductResponse
import ru.k.kbook_api.grpc.product.SortDirectionDto
import ru.k.kbook_api.grpc.product.SortFieldDto
import ru.k.kbook_api.grpc.product.UpdateProductRequest
import java.time.Instant

fun CreateProductRequestDto.toGrpc(): CreateProductRequest {
    return CreateProductRequest.newBuilder()
        .setName(name)
        .addAllImages(
            images.map { img ->
                ru.k.kbook_api.grpc.product.ImageInput.newBuilder()
                    .setUrl(img.url ?: "")
                    .setImage(img.image?.toByteString() ?: ByteString.EMPTY)
                    .setContentType(img.contentType.toGrpc())
                    .build()
            },
        )
        .setCaloricity(caloricity)
        .setProtein(protein)
        .setFat(fat)
        .setCarb(carb)
        .setDescription(description.orEmpty())
        .setCategory(category.toGrpc())
        .setCookingRequired(cookingRequired.toGrpc())
        .addAllFlags(flags.map { it.toGrpc() })
        .build()
}

fun GetProductRequestDto.toGrpc(): GetProductRequest =
    GetProductRequest.newBuilder()
        .setId(id).build()

fun UpdateProductRequestDto.toGrpc(): UpdateProductRequest {
    val builder = UpdateProductRequest.newBuilder()
        .setId(id)

    name?.let { builder.setName(it) }
    caloricity?.let { builder.setCaloricity(it) }
    protein?.let { builder.setProtein(it) }
    fat?.let { builder.setFat(it) }
    carb?.let { builder.setCarb(it) }
    description?.let { builder.setDescription(it) }
    category?.let { builder.setCategory(it.toGrpc()) }
    cookingRequired?.let { builder.setCookingRequired(it.toGrpc()) }

    images?.let { imgs ->
        builder.addAllImages(
            imgs.map { img ->
                ru.k.kbook_api.grpc.product.ImageInput.newBuilder()
                    .setUrl(img.url ?: "")
                    .setImage(img.image?.toByteString() ?: ByteString.EMPTY)
                    .setContentType(img.contentType.toGrpc())
                    .build()
            },
        )
    }

    flags?.let { flags ->
        builder.addAllFlags(flags.map { it.toGrpc() })
    }

    return builder.build()
}

fun DeleteProductRequestDto.toGrpc(): DeleteProductRequest =
    DeleteProductRequest.newBuilder().setId(id).build()

fun ListProductsRequestDto.toGrpc(): ListProductsRequest {
    return ListProductsRequest.newBuilder().apply {
        searchQuery?.let { setSearchQuery(it) }
        if (!categories.isNullOrEmpty()) {
            addAllCategories(categories.map { it.toGrpc() })
        }
        if (!cookingRequired.isNullOrEmpty()) {
            addAllCookingRequired(cookingRequired.map { it.toGrpc() })
        }
        if (!flags.isNullOrEmpty()) {
            addAllFlags(flags.map { it.toGrpc() })
        }
        sortBy?.let { setSortBy(it) }
        sortDirection?.let { setSortDirection(it) }
        setLimit(limit)
        setOffset(offset)
    }.build()
}

fun GetProductsForDishRequestDto.toGrpc(): GetProductsForDishRequest =
    GetProductsForDishRequest.newBuilder()
        .addAllProductIds(productIds)
        .build()

fun ProductDto.toKotlin(): Product {
    return Product(
        id = id,
        name = name,
        images = imagesList.map { img ->
            ProductImage(
                id = img.id,
                url = img.url,
                image = if (img.image.isEmpty) null else img.image.toByteArray(),
                contentType = ContentType.valueOf(img.contentType.name),
            )
        },
        caloricity = caloricity,
        protein = protein,
        fat = fat,
        carb = carb,
        description = description,
        category = ProductCategory.valueOf(category.name),
        cookingRequired = CookingRequired.valueOf(cookingRequired.name),
        flags = flagsList.map { ProductFlag.valueOf(it.name) },
        createdAt = Instant.ofEpochSecond(createdAt.seconds),
        updatedAt = Instant.ofEpochSecond(updatedAt.seconds),
    )
}

fun ProductResponse.toKotlin(): ProductResponseDto {
    return ProductResponseDto(
        product = product.toKotlin(),
        success = success,
        message = message,
    )
}

fun ListProductsResponse.toKotlin(): ListProductsResponseDto {
    return ListProductsResponseDto(
        products = productsList.map { it.toKotlin() },
        totalCount = totalCount,
        success = success,
        message = message,
    )
}

fun DeleteProductResponse.toKotlin(): DeleteProductResponseDto {
    return DeleteProductResponseDto(
        success = success,
        message = message,
        usedInDishes = usedInDishesList,
    )
}

fun ProductCategory.toGrpc(): ProductCategoryDto =
    when (this) {
        ProductCategory.FROZEN -> ProductCategoryDto.FROZEN
        ProductCategory.MEAT -> ProductCategoryDto.MEAT
        ProductCategory.VEGETABLES -> ProductCategoryDto.VEGETABLES
        ProductCategory.GREENS -> ProductCategoryDto.GREENS
        ProductCategory.SPICES -> ProductCategoryDto.SPICES
        ProductCategory.CEREALS -> ProductCategoryDto.CEREALS
        ProductCategory.CANNED -> ProductCategoryDto.CANNED
        ProductCategory.LIQUID -> ProductCategoryDto.LIQUID
        ProductCategory.SWEETS -> ProductCategoryDto.SWEETS
    }

fun CookingRequired.toGrpc(): CookingRequiredDto =
    when (this) {
        CookingRequired.READY_TO_EAT -> CookingRequiredDto.READY_TO_EAT
        CookingRequired.SEMI_FINISHED -> CookingRequiredDto.SEMI_FINISHED
        CookingRequired.REQUIRES_COOKING -> CookingRequiredDto.REQUIRES_COOKING
    }

fun ProductFlag.toGrpc(): ProductFlagDto =
    when (this) {
        ProductFlag.VEGAN -> ProductFlagDto.VEGAN
        ProductFlag.GLUTEN_FREE -> ProductFlagDto.GLUTEN_FREE
        ProductFlag.SUGAR_FREE -> ProductFlagDto.SUGAR_FREE
    }

fun ContentType.toGrpc(): ContentTypeDto =
    when (this) {
        ContentType.IMAGE -> ContentTypeDto.IMAGE
        ContentType.URL -> ContentTypeDto.URL
    }

fun SortField.toGrpc(): SortFieldDto =
    when (this) {
        SortField.NAME -> SortFieldDto.NAME
        SortField.CALORICITY -> SortFieldDto.CALORICITY
        SortField.PROTEIN -> SortFieldDto.PROTEIN
        SortField.FAT -> SortFieldDto.FAT
        SortField.CARB -> SortFieldDto.CARB
    }

fun SortDirection.toGrpc(): SortDirectionDto =
    when (this) {
        SortDirection.ASC -> SortDirectionDto.ASC
        SortDirection.DESC -> SortDirectionDto.DESC
    }

fun ProductCategoryDto.toKotlin(): ProductCategory =
    when (this) {
        ProductCategoryDto.FROZEN -> ProductCategory.FROZEN
        ProductCategoryDto.MEAT -> ProductCategory.MEAT
        ProductCategoryDto.VEGETABLES -> ProductCategory.VEGETABLES
        ProductCategoryDto.GREENS -> ProductCategory.GREENS
        ProductCategoryDto.SPICES -> ProductCategory.SPICES
        ProductCategoryDto.CEREALS -> ProductCategory.CEREALS
        ProductCategoryDto.CANNED -> ProductCategory.CANNED
        ProductCategoryDto.LIQUID -> ProductCategory.LIQUID
        ProductCategoryDto.SWEETS -> ProductCategory.SWEETS
        else -> ProductCategory.FROZEN
    }

fun CookingRequiredDto.toKotlin(): CookingRequired =
    when (this) {
        CookingRequiredDto.READY_TO_EAT -> CookingRequired.READY_TO_EAT
        CookingRequiredDto.SEMI_FINISHED -> CookingRequired.SEMI_FINISHED
        CookingRequiredDto.REQUIRES_COOKING -> CookingRequired.REQUIRES_COOKING
        CookingRequiredDto.UNRECOGNIZED -> throw IllegalArgumentException("Wrong enum value")
    }

fun ProductFlagDto.toKotlin(): ProductFlag =
    when (this) {
        ProductFlagDto.VEGAN -> ProductFlag.VEGAN
        ProductFlagDto.GLUTEN_FREE -> ProductFlag.GLUTEN_FREE
        ProductFlagDto.SUGAR_FREE -> ProductFlag.SUGAR_FREE
        else -> ProductFlag.VEGAN
    }

fun ContentTypeDto.toKotlin(): ContentType =
    when (this) {
        ContentTypeDto.IMAGE -> ContentType.IMAGE
        ContentTypeDto.URL -> ContentType.URL
        else -> ContentType.IMAGE
    }

fun SortFieldDto.toKotlin(): SortField =
    when (this) {
        SortFieldDto.NAME -> SortField.NAME
        SortFieldDto.CALORICITY -> SortField.CALORICITY
        SortFieldDto.PROTEIN -> SortField.PROTEIN
        SortFieldDto.FAT -> SortField.FAT
        SortFieldDto.CARB -> SortField.CARB
        else -> SortField.NAME
    }

fun SortDirectionDto.toKotlin(): SortDirection =
    when (this) {
        SortDirectionDto.ASC -> SortDirection.ASC
        SortDirectionDto.DESC -> SortDirection.DESC
        else -> SortDirection.ASC
    }