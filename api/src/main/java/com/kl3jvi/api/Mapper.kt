package com.kl3jvi.api

import kotlin.reflect.KClass

/**
 * Annotation to instruct the generation of an extension function that maps the annotated
 * data class to one or more specified target data classes.
 *
 * By annotating a data class with this annotation, a mapper function will be automatically
 * generated, streamlining the process of data transformation between the source (annotated) class
 * and the specified target classes.
 *
 * ## Parameters:
 * - [target]: An array of target data classes to which the source data class should be mapped.
 *
 * ## Example:
 * Suppose there are three data classes: `User`, `UserDTO`, and `UserView`. If you want to generate
 * mapper functions that map the `User` class to both `UserDTO` and `UserView`, you would annotate
 * the `User` class as follows:
 * ```
 * @Mapper(target = [UserDTO::class, UserView::class])
 * data class User(val name: String, val age: Int, val address: String)
 * ```
 * In this example, two separate mapper functions will be generated: one for mapping `User` to
 * `UserDTO` and another for mapping `User` to `UserView`.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mapper(val target: Array<KClass<*>>)
