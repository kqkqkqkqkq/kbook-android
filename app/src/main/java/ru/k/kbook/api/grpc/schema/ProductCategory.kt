package ru.k.kbook.api.grpc.schema

enum class ProductCategory {
    FROZEN,
    MEAT,
    VEGETABLES,
    GREENS,
    SPICES,
    CEREALS,
    CANNED,
    LIQUID,
    SWEETS,
    ;

    fun getRu() = when (this) {
        ProductCategory.FROZEN -> "Замороженное"
        ProductCategory.MEAT -> "Мясо"
        ProductCategory.VEGETABLES -> "Овощи"
        ProductCategory.GREENS -> "Зелень"
        ProductCategory.SPICES -> "Специи"
        ProductCategory.CEREALS -> "Крупы"
        ProductCategory.CANNED -> "Консервы"
        ProductCategory.LIQUID -> "Жидкость"
        ProductCategory.SWEETS -> "Сладости"
    }
}