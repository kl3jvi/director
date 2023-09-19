package com.kl3jvi.api

import kotlin.reflect.KClass

/**
 * An annotation to indicate that a mapper extension function should be generated for this data class.
 * The [target] parameter is used to specify the target data class for mapping.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mapper(val target: Array<KClass<*>>)
