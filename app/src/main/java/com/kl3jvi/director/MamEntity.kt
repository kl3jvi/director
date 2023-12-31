package com.kl3jvi.director

import com.kl3jvi.api.EditableMapper
import com.kl3jvi.api.Mapper

@EditableMapper(target = MamUi::class, editableFields = ["name", "age"])
@Mapper(target = [MamNetwork::class])
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

data class MamUi(
    val name: String,
    val age: Int,
)

fun tester() {
    val mamEntity = MamEntity(
        name = "Kl3jvi",
        age = 18,
        isAdult = true,
        isChild = false,
        isTeen = false,
        money = 100.0,
        money2 = 100.0f,
        money3 = 100L,
    )
}
