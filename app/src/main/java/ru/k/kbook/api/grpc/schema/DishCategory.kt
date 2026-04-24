package ru.k.kbook.api.grpc.schema

enum class DishCategory {
    DESSERT,
    FIRST,
    SECOND,
    DRINK,
    SALAD,
    SOUP,
    SNACK,
    ;

    fun getRu(): String = when(this) {
        DishCategory.DESSERT -> "Десерт"
        DishCategory.FIRST -> "Первое"
        DishCategory.SECOND -> "Второе"
        DishCategory.DRINK -> "Напиток"
        DishCategory.SALAD -> "Салат"
        DishCategory.SOUP -> "Суп"
        DishCategory.SNACK -> "Закуска"
    }
}
