package com.kl3jvi.processor.property

import com.google.devtools.ksp.processing.KSPLogger
import com.kl3jvi.processor.mapper.ProcessedProperties
import com.kl3jvi.processor.util.asTypeName
import com.squareup.kotlinpoet.FunSpec

/**
 * Generates a mapper function for the given properties.
 *
 * This function creates a KotlinPoet [FunSpec] representing the mapper extension function.
 *
 * @param processedProperties The properties processed from the annotated class.
 * @return a [FunSpec] representing the mapper function.
 */
fun generateMapperFunction(processedProperties: ProcessedProperties, logger: KSPLogger): FunSpec {
    val targetTypeName = processedProperties.targetClass?.asTypeName()!!
    val functionName = "to${processedProperties.targetClass.simpleName.asString()}"

    return FunSpec.builder(functionName)
        .receiver(processedProperties.sourceClass.asTypeName())
        .returns(targetTypeName)
        .addCode(buildCodeBlock(targetTypeName, processedProperties))
        .build()
}
