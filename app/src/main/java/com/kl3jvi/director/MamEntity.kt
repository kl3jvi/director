package com.kl3jvi.director

import com.kl3jvi.api.EditableMapper

@EditableMapper(
    MamNetwork::class,
    ["name", "age"]
)
data class MamEntity(
    val name: String,
    val age: Int,
    val isAdult: Boolean,
    val isChild: Boolean,
    val isTeen: Boolean,
    val money: Double,
    val money2: Float,
    val money3: Long,
)

data class MamNetwork(
    val name: String,
    val age: Int,
    val isAdult: Boolean,
    val isChild: Boolean,
    val isTeen: Boolean,
    val money: Double,
    val money2: Float,
    val money3: Long,
)

fun tester() {
}
