package ru.k.kbook.api.grpc.schema

enum class CookingRequired {
    READY_TO_EAT,
    SEMI_FINISHED,
    REQUIRES_COOKING,
    ;

    fun getRu() = when(this) {
        CookingRequired.READY_TO_EAT -> "Готовый к употреблению"
        CookingRequired.SEMI_FINISHED -> "Полуфабрикат"
        CookingRequired.REQUIRES_COOKING -> "Требует приготовления"
    }
}