package ru.k.kbook.api.grpc.schema

enum class ProductFlag {
    VEGAN,
    GLUTEN_FREE,
    SUGAR_FREE,
    ;
    fun getRu() = when(this) {
        ProductFlag.VEGAN -> "Веган"
        ProductFlag.GLUTEN_FREE -> "Без глютена"
        ProductFlag.SUGAR_FREE -> "Без сахара"
    }
}