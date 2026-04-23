package ru.k.kbook.data.dish.mapper

import com.google.protobuf.ByteString
import ru.k.kbook.api.grpc.request.CreateDishRequestDto
import ru.k.kbook.api.grpc.request.DeleteDishRequestDto
import ru.k.kbook.api.grpc.request.GetDishRequestDto
import ru.k.kbook.api.grpc.request.ListDishesRequestDto
import ru.k.kbook.api.grpc.request.UpdateDishRequestDto
import ru.k.kbook.api.grpc.response.DeleteDishResponseDto
import ru.k.kbook.api.grpc.response.DishListResponseDto
import ru.k.kbook.api.grpc.response.DishResponseDto
import ru.k.kbook.api.grpc.response.ValidateDishResponseDto
import ru.k.kbook.api.grpc.schema.Dish
import ru.k.kbook.api.grpc.schema.DishCategory
import ru.k.kbook.api.grpc.schema.DishFlag
import ru.k.kbook.api.grpc.schema.DishImage
import ru.k.kbook.api.grpc.schema.DishProduct
import ru.k.kbook_api.grpc.dish.CreateDishRequest
import ru.k.kbook_api.grpc.dish.DeleteDishRequest
import ru.k.kbook_api.grpc.dish.DeleteDishResponse
import ru.k.kbook_api.grpc.dish.DishComposition
import ru.k.kbook_api.grpc.dish.DishFlags
import ru.k.kbook_api.grpc.dish.DishImages
import ru.k.kbook_api.grpc.dish.DishListResponse
import ru.k.kbook_api.grpc.dish.GetDishRequest
import ru.k.kbook_api.grpc.dish.ListDishesRequest
import ru.k.kbook_api.grpc.dish.UpdateDishRequest
import ru.k.kbook_api.grpc.dish.ValidateDishResponse
import java.time.Instant
import ru.k.kbook_api.grpc.dish.Dish as GrpcDish
import ru.k.kbook_api.grpc.dish.DishCategory as GrpcDishCategory
import ru.k.kbook_api.grpc.dish.DishFlag as GrpcDishFlag
import ru.k.kbook_api.grpc.dish.DishImage as GrpcDishImage
import ru.k.kbook_api.grpc.dish.DishProduct as GrpcDishProduct

fun CreateDishRequestDto.toGrpc(): CreateDishRequest {
    val builder = CreateDishRequest.newBuilder()
        .setName(name)
        .setPortionSize(portionSize)
    builder.addAllImages(images.map { image -> image.toGrpc() })
    builder.addAllComposition(composition.map { item -> item.toGrpc() })
    if (category != null) builder.category = category.toGrpc()
    builder.addAllFlags(flags.map { flag -> flag.toGrpc() })
    if (caloricity != null) builder.caloricity = caloricity
    if (protein != null) builder.protein = protein
    if (fat != null) builder.fat = fat
    if (carb != null) builder.carb = carb
    return builder.build()
}

fun UpdateDishRequestDto.toGrpc(): UpdateDishRequest {
    val builder = UpdateDishRequest.newBuilder().setId(id)
    if (name != null) builder.name = name
    if (images != null) builder.images =
        DishImages.newBuilder().addAllItems(images.map { image -> image.toGrpc() }).build()
    if (composition != null) builder.composition =
        DishComposition.newBuilder().addAllItems(composition.map { item -> item.toGrpc() }).build()
    if (portionSize != null) builder.portionSize = portionSize
    if (category != null) builder.category = category.toGrpc()
    if (flags != null) builder.flags =
        DishFlags.newBuilder().addAllItems(flags.map { flag -> flag.toGrpc() }).build()
    if (caloricity != null) builder.caloricity = caloricity
    if (protein != null) builder.protein = protein
    if (fat != null) builder.fat = fat
    if (carb != null) builder.carb = carb
    return builder.build()
}

fun GetDishRequestDto.toGrpc(): GetDishRequest = GetDishRequest.newBuilder().setId(id).build()
fun DeleteDishRequestDto.toGrpc(): DeleteDishRequest =
    DeleteDishRequest.newBuilder().setId(id).build()

fun ListDishesRequestDto.toGrpc(): ListDishesRequest =
    ListDishesRequest.newBuilder()
        .apply {
            searchQuery?.let { setSearchQuery(it) }
            categories?.let { addAllCategories(it.map { category -> category.toGrpc() }) }
            flags?.let { addAllFlags(it.map { flag -> flag.toGrpc() }) }
            limit?.let { setLimit(it) }
            offset?.let { setOffset(it) }
        }
        .build()

fun GrpcDish.toKotlin(): Dish =
    Dish(
        id = id,
        name = name,
        images = imagesList.map { it.toKotlin() },
        caloricity = caloricity,
        protein = protein,
        fat = fat,
        carb = carb,
        composition = compositionList.map { it.toKotlin() },
        portionSize = portionSize,
        category = category.toKotlin(),
        flags = flagsList.map { it.toKotlin() },
        createdAt = Instant.ofEpochSecond(createdAt.seconds),
        updatedAt = Instant.ofEpochSecond(updatedAt.seconds),
    )

fun DishListResponse.toKotlin(): DishListResponseDto = DishListResponseDto(
    dishes = dishesList.map { it.toKotlin() },
    total = total,
)

fun DeleteDishResponse.toKotlin(): DeleteDishResponseDto = DeleteDishResponseDto(success = success)
fun ValidateDishResponse.toKotlin(): ValidateDishResponseDto = ValidateDishResponseDto(
    valid = valid,
    errors = errorsList,
    calculatedCaloricity = calculatedCaloricity,
    calculatedProtein = calculatedProtein,
    calculatedFat = calculatedFat,
    calculatedCarb = calculatedCarb,
    availableFlags = availableFlagsList.map { it.toKotlin() },
)

fun DishCategory.toGrpc(): GrpcDishCategory = GrpcDishCategory.valueOf(name)
fun DishFlag.toGrpc(): GrpcDishFlag = GrpcDishFlag.valueOf(name)
fun GrpcDishCategory.toKotlin(): DishCategory = DishCategory.valueOf(name)
fun GrpcDishFlag.toKotlin(): DishFlag = DishFlag.valueOf(name)

private fun DishImage.toGrpc(): GrpcDishImage = GrpcDishImage.newBuilder()
    .setId(id)
    .setUrl(url.orEmpty())
    .setImage(if (image == null) ByteString.EMPTY else ByteString.copyFrom(image))
    .setContentType(contentType)
    .build()

private fun GrpcDishImage.toKotlin(): DishImage = DishImage(
    id = id,
    url = url.ifBlank { null },
    image = if (image.isEmpty) null else image.toByteArray(),
    contentType = contentType,
)

private fun DishProduct.toGrpc(): GrpcDishProduct = GrpcDishProduct.newBuilder()
    .setProductId(productId)
    .setProductName(productName)
    .setQuantity(quantity)
    .build()

private fun GrpcDishProduct.toKotlin(): DishProduct = DishProduct(
    productId = productId,
    productName = productName,
    quantity = quantity,
)

fun GrpcDish.toResponse(): DishResponseDto = DishResponseDto(dish = toKotlin())
