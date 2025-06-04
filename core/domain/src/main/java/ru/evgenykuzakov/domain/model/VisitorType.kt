package ru.evgenykuzakov.domain.model

enum class VisitorType(val type: String) {
    VIEW("view"), SUBSCRIPTION("subscription"), UNSUBSCRIPTION("unsubscription")
}