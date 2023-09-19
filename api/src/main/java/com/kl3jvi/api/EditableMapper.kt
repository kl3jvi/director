package com.kl3jvi.api

import kotlin.reflect.KClass

/**
 * Annotation to instruct the generation of an extension function that maps the annotated
 * data class to a target data class, focusing on the provided editable fields.
 *
 * Usage of this annotation will result in a generated mapper function that specifically
 * maps only the fields declared in [editableFields] from the source (annotated) class to
 * the [target] class.
 *
 * This is particularly useful for scenarios where only a subset of fields in a data class
 * should be editable and reflected in another data class representation.
 *
 * ## Parameters:
 * - [target]: The target data class to which the source data class should be mapped.
 * - [editableFields]: A list of field names from the source data class that should be
 *   considered editable and thus included in the mapping process.
 *
 * ## Example:
 * Suppose there are two data classes: `User` and `UserDTO`. If you want to generate a mapper
 * function that only maps the `name` and `age` fields from `User` to `UserDTO`, you would annotate
 * the `User` class as follows:
 * ```
 * @EditableMapper(target = UserDTO::class, editableFields = ["name", "age"])
 * data class User(val name: String, val age: Int, val address: String)
 * ```
 * In this example, the generated mapper will ensure that only the `name` and `age` fields are
 * editable and mapped to `UserDTO`, leaving out the `address` field.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EditableMapper(val target: KClass<*>, val editableFields: Array<String>)
