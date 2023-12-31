package com.kl3jvi.processor.mapper

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.kl3jvi.processor.property.generateMapperFunction
import com.kl3jvi.processor.util.addTopComment
import com.squareup.kotlinpoet.FileSpec
import java.security.MessageDigest

fun generateAndWriteMapperFile(
    processedPropertiesList: List<ProcessedProperties?>,
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
) {
    val generatedFiles = codeGenerator.generatedFile.map { it.name }.toSet()

    processedPropertiesList.forEach { processedProperties ->
        if (processedProperties != null) {
            val mapperFunction = generateMapperFunction(processedProperties, logger)

            // Create a unique file name based on source and target classes
            val hashedTargetName = processedProperties.targetClass?.qualifiedName?.asString()
                ?.toByteArray()
                ?.let { bytes -> MessageDigest.getInstance("SHA-1").digest(bytes) }
                ?.take(1) // taking the first byte for brevity
                ?.joinToString("") { "%02x".format(it) }
                ?: ""

            val fileName =
                "${processedProperties.sourceClass.simpleName.asString()}Mapper_$hashedTargetName"

            if (fileName !in generatedFiles) {
                val fileSpec = FileSpec.builder(
                    processedProperties.sourceClass.packageName.asString(),
                    fileName,
                ).apply {
                    addTopComment()
                    addFunction(mapperFunction)
                }.build()

                val file = codeGenerator.createNewFile(
                    dependencies = Dependencies(aggregating = true, sources = arrayOf()),
                    packageName = processedProperties.sourceClass.packageName.asString(),
                    fileName = fileName,
                )

                file.bufferedWriter().use { writer ->
                    fileSpec.writeTo(writer)
                }
            }
        }
    }
}

fun generateAndWriteEditableMapperFile(
    processedProperties: ProcessedProperties,
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
) {
    val mapperFunction = generateMapperFunction(processedProperties, logger)

    val fileSpec = FileSpec.builder(
        processedProperties.sourceClass.packageName.asString(),
        "${processedProperties.sourceClass.simpleName.asString()}Mapper",
    ).addTopComment()
        .addFunction(mapperFunction)
        .build()

    val file = codeGenerator.createNewFile(
        dependencies = Dependencies(aggregating = true, sources = arrayOf()),
        packageName = processedProperties.sourceClass.packageName.asString(),
        fileName = "${processedProperties.sourceClass.simpleName.asString()}Mapper",
    )

    file.bufferedWriter().use { writer ->
        fileSpec.writeTo(writer)
    }
}