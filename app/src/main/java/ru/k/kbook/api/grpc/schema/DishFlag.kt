package ru.k.kbook.api.grpc.schema

enum class DishFlag {
    VEGAN,
    GLUTEN_FREE,
    SUGAR_FREE,
    ;
    fun getRu(): String = when(this) {
        DishFlag.VEGAN -> "Веган"
        DishFlag.GLUTEN_FREE -> "Без глютена"
        DishFlag.SUGAR_FREE -> "Без сахара"
    }
}
