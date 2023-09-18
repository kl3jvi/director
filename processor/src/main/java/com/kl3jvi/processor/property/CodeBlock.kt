package com.kl3jvi.processor.property

import com.kl3jvi.processor.mapper.ProcessedProperties
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.joinToCode

fun buildCodeBlock(
    targetType: TypeName,
    processedProperties: ProcessedProperties,
): CodeBlock {
    val codeBuilder = CodeBlock.builder().add("return %T(\n", targetType)

    val propertyCodeBlocks = processedProperties.properties.map { prop ->
        when {
            prop.isMatching -> CodeBlock.of("    %L = this.%L", prop.targetName, prop.sourceName)
            else -> CodeBlock.of("    %L = null", prop.targetName)
        }
    }
    codeBuilder.add(propertyCodeBlocks.joinToCode(separator = ",\n"))
    codeBuilder.add("\n)") // Close the statement properly

    return codeBuilder.build()
}
