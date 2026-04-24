package ru.k.kbook.api.grpc.schema

enum class SortField {
    NAME,
    CALORICITY,
    PROTEIN,
    FAT,
    CARB,
    ;

    fun getRu() = when(this) {
        SortField.NAME -> "Название"
        SortField.CALORICITY -> "Калорийность"
        SortField.PROTEIN -> "Белки"
        SortField.FAT -> "Жиры"
        SortField.CARB -> "Углеводы"
    }
}