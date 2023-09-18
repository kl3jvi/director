package com.kl3jvi.api

import kotlin.reflect.KClass

/**
 * An annotation to indicate that a mapper extension function should be generated for this data class.
 * The [target] parameter is used to specify the target data class for mapping.
 * The [editableFields] parameter is used to specify which fields should be editable in the generated
 * extension function.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EditableMapper(val target: KClass<*>, val editableFields: Array<String>)
