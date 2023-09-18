package com.kl3jvi.api
/**
 * An annotation to indicate that a mapper extension function should be generated for this data class.
 * The [target] parameter is used to specify the target data class for mapping.
 * The [ignoreFields] parameter is used to specify which fields should be ignored in the generated
 * extension function.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MapperIgnore(val fields: Array<String>)
