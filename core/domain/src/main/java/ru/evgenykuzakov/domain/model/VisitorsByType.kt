package ru.evgenykuzakov.domain.model

data class VisitorsByTypeResult(
    val view: VisitorsByType,
    val subscribers: VisitorsByType,
    val unsubscribers: VisitorsByType,

)

data class VisitorsByType(
    val type: VisitorType,
    val count: Int
)

enum class VisitorType(val type: String) {
    VIEW("view"),
    SUBSCRIPTION("subscription"),
    UNSUBSCRIPTION("unsubscription")
}