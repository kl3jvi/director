package com.kl3jvi.processor.util

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.kl3jvi.api.EditableMapper
import com.kl3jvi.api.Mapper
import kotlin.reflect.KClass

internal fun KSClassDeclaration.validateModifierContainsMapper(logger: KSPLogger): Boolean {
    val annotationName = when {
        this.hasAnnotation(Mapper::class) -> Mapper::class.simpleName
        this.hasAnnotation(EditableMapper::class) -> EditableMapper::class.simpleName
        else -> "UnknownAnnotation" // This should ideally never be reached.
    }

    if (!modifiers.contains(Modifier.DATA)) {
        logger.error(
            "$annotationName annotation can only be applied to data classes, " +
                "but it was applied to ${classKind.describeClassKind()}.",
        )
        return false
    }
    return true
}

// Helper function to check if a KSClassDeclaration has a specific annotation.
internal fun KSClassDeclaration.hasAnnotation(annotationClass: KClass<*>): Boolean {
    return this.annotations.any { it.shortName.asString() == annotationClass.simpleName }
}

internal fun ClassKind.describeClassKind(): String {
    return when (this) {
        ClassKind.INTERFACE -> "an interface"
        ClassKind.CLASS -> "a class"
        ClassKind.ENUM_CLASS -> "an enum class"
        ClassKind.ENUM_ENTRY -> "an enum entry"
        ClassKind.OBJECT -> "an object"
        ClassKind.ANNOTATION_CLASS -> "an annotation class"
    }
}
