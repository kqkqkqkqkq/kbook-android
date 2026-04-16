package ru.k.kbook.data.product.mapper

import com.google.protobuf.kotlin.toByteString
import ru.k.kbook.api.grpc.request.*
import ru.k.kbook.api.grpc.response.*
import ru.k.kbook.api.grpc.schema.*
import ru.k.kbook_api.grpc.product.*
import ru.k.kbook_api.grpc.product.ProductDto
import java.time.Instant

fun CreateProductRequestDto.toGrpc(): CreateProductRequest {
    println("create request: $this")
    return CreateProductRequest.newBuilder()
        .setName(name)
        .addAllImages(images.map { img ->
            ru.k.kbook_api.grpc.product.ImageInput.newBuilder()
                .setUrl(img.url ?: "")
                .setImage(img.image?.toByteString() ?: com.google.protobuf.ByteString.EMPTY)
                .setContentType(img.contentType.toGrpc())
                .build()
        })
        .setCaloricity(caloricity)
        .setProtein(protein)
        .setFat(fat)
        .setCarb(carb)
        .setDescription(description)
        .setCategory(category.toGrpc())
        .setCookingRequired(cookingRequired.toGrpc())
        .addAllFlags(flags.map { it.toGrpc() })
        .build()
}

fun GetProductRequestDto.toGrpc(): GetProductRequest =
    GetProductRequest.newBuilder()
        .setId(id).build()

fun UpdateProductRequestDto.toGrpc(): UpdateProductRequest {
    return UpdateProductRequest.newBuilder()
        .setId(id)
        .apply {
            name?.let { setName(it) }
            if (images != null) {
                addAllImages(images.map { img ->
                    ru.k.kbook_api.grpc.product.ImageInput.newBuilder()
                        .setUrl(img.url ?: "")
                        .setImage(img.image?.toByteString() ?: com.google.protobuf.ByteString.EMPTY)
                        .setContentType(img.contentType.toGrpc())
                        .build()
                })
            }
            caloricity.let { setCaloricity(it) }
            protein.let { setProtein(it) }
            fat.let { setFat(it) }
            carb.let { setCarb(it) }
            description?.let { setDescription(it) }
            category?.let { setCategory(it) }
            cookingRequired?.let { setCookingRequired(it) }
            addAllFlags(flags?.map { it.toGrpc() })
        }
        .build()
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
        limit.let { setLimit(it) }
        offset.let { setOffset(it) }
    }.build()
}

fun GetProductsForDishRequestDto.toGrpc(): GetProductsForDishRequest =
    GetProductsForDishRequest.newBuilder()
        .addAllProductIds(productIds)
        .build()

fun ProductDto.toKotlin(): Product {
    return Product (
        id = id,
        name = name,
        images = imagesList.map { img ->
            ProductImage(
                id = img.id,
                url = img.url,
                image = if (img.image.isEmpty) null else img.image.toByteArray(),
                contentType = ContentType.valueOf(img.contentType.name)
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
        updatedAt = Instant.ofEpochSecond(updatedAt.seconds)
    )
}

fun ProductResponse.toKotlin(): ProductResponseDto {
    return ProductResponseDto(
        product = product.toKotlin(),
        success = success,
        message = message
    )
}

fun ListProductsResponse.toKotlin(): ListProductsResponseDto {
    return ListProductsResponseDto(
        products = productsList.map { it.toKotlin() },
        totalCount = totalCount,
        success = success,
        message = message
    )
}

fun DeleteProductResponse.toKotlin(): DeleteProductResponseDto {
    return DeleteProductResponseDto(
        success = success,
        message = message,
        usedInDishes = usedInDishesList
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
        else -> throw IllegalArgumentException("Unrecognized enum value")
    }

fun ProductCategoryDto.toKotlin(): ProductCategoryDto =
    when (this) {
        ProductCategoryDto.FROZEN -> ProductCategoryDto.FROZEN
        ProductCategoryDto.MEAT -> ProductCategoryDto.MEAT
        ProductCategoryDto.VEGETABLES -> ProductCategoryDto.VEGETABLES
        ProductCategoryDto.GREENS -> ProductCategoryDto.GREENS
        ProductCategoryDto.SPICES -> ProductCategoryDto.SPICES
        ProductCategoryDto.CEREALS -> ProductCategoryDto.CEREALS
        ProductCategoryDto.CANNED -> ProductCategoryDto.CANNED
        ProductCategoryDto.LIQUID -> ProductCategoryDto.LIQUID
        ProductCategoryDto.SWEETS -> ProductCategoryDto.SWEETS
        else -> ProductCategoryDto.FROZEN
    }

fun CookingRequiredDto.toKotlin(): CookingRequiredDto =
    when (this) {
        CookingRequiredDto.READY_TO_EAT -> CookingRequiredDto.READY_TO_EAT
        CookingRequiredDto.SEMI_FINISHED -> CookingRequiredDto.SEMI_FINISHED
        CookingRequiredDto.REQUIRES_COOKING -> CookingRequiredDto.REQUIRES_COOKING
        else -> CookingRequiredDto.READY_TO_EAT
    }

fun ProductFlagDto.toKotlin(): ProductFlagDto =
    when (this) {
        ProductFlagDto.VEGAN -> ProductFlagDto.VEGAN
        ProductFlagDto.GLUTEN_FREE -> ProductFlagDto.GLUTEN_FREE
        ProductFlagDto.SUGAR_FREE -> ProductFlagDto.SUGAR_FREE
        else -> ProductFlagDto.VEGAN
    }

fun ContentTypeDto.toKotlin(): ContentTypeDto =
    when (this) {
        ContentTypeDto.IMAGE -> ContentTypeDto.IMAGE
        ContentTypeDto.URL -> ContentTypeDto.URL
        else -> ContentTypeDto.IMAGE
    }

fun SortFieldDto.toKotlin(): SortFieldDto =
    when (this) {
        SortFieldDto.NAME -> SortFieldDto.NAME
        SortFieldDto.CALORICITY -> SortFieldDto.CALORICITY
        SortFieldDto.PROTEIN -> SortFieldDto.PROTEIN
        SortFieldDto.FAT -> SortFieldDto.FAT
        SortFieldDto.CARB -> SortFieldDto.CARB
        else -> SortFieldDto.NAME
    }

fun SortDirectionDto.toKotlin(): SortDirectionDto =
    when (this) {
        SortDirectionDto.ASC -> SortDirectionDto.ASC
        SortDirectionDto.DESC -> SortDirectionDto.DESC
        else -> SortDirectionDto.ASC
    }