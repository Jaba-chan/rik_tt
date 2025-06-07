package ru.evgenykuzakov.domain.model

data class VisitorsByType(
    val type: VisitorType,
    val count: Int
)

enum class VisitorType(val type: String) {
    VIEW("view"),
    SUBSCRIPTION("subscription"),
    UNSUBSCRIPTION("unsubscription")
}