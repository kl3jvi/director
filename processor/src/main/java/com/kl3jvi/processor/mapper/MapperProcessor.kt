package com.kl3jvi.processor.mapper

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.kl3jvi.api.EditableMapper
import com.kl3jvi.api.Mapper
import com.kl3jvi.api.MapperIgnore
import com.kl3jvi.processor.property.ClassPropertyProcessor
import com.kl3jvi.processor.util.validateModifierContainsMapper
import kotlin.reflect.KClass

class MapperProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    /**
     * The main processing function.
     *
     * This function searches for classes annotated with [Mapper], processes their properties, and
     * generates mapper extension functions.
     *
     * @param resolver The [Resolver] provides APIs for symbol resolution.
     * @return a list of annotated symbols that were not processed, which is always empty for this implementation.
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val propertyProcessor = ClassPropertyProcessor()

        // Retrieve all classes annotated with Mapper
        val annotatedClasses = getDeclarationsAnnotatedWith(Mapper::class, resolver)
            .filter { it.validate() && it.validateModifierContainsMapper(logger) }
            .mapNotNull { classDeclaration ->
                val targetClassType = classDeclaration.getMapperAnnotation()
                    ?.getTargetClassType()
                    ?: return@mapNotNull null

                val targetClassName = targetClassType
                    .declaration
                    .qualifiedName
                    ?.asString()
                    ?: return@mapNotNull null

                propertyProcessor.processClass(classDeclaration, targetClassName, resolver)
            }

        annotatedClasses.forEach { processedProperties ->
            generateAndWriteMapperFile(
                processedProperties,
                codeGenerator,
                logger,
            )
        }

        val editableMapperAnnotatedClasses =
            getDeclarationsAnnotatedWith(EditableMapper::class, resolver)
                .filter { it.validateModifierContainsMapper(logger) }
                .mapNotNull { classDeclaration ->
                    val editableFields = classDeclaration.getEditableFieldsFromAnnotation()
                    if (editableFields == null) {
                        logger.error("Could not extract editable fields for ${classDeclaration.qualifiedName?.asString()}")
                        return@mapNotNull null
                    }

                    propertyProcessor.processClassForEditableMapper(
                        classDeclaration,
                        editableFields,
                        resolver
                    )
                }

        editableMapperAnnotatedClasses.forEach { processedProperties ->
            generateAndWriteEditableMapperFile(
                processedProperties,
                codeGenerator,
                logger,
            )
        }


//        val mapperIgnoreAnnotatedClasses =
//            getDeclarationsAnnotatedWith(MapperIgnore::class, resolver)
//                .filter { it.validateModifierContainsMapper(logger) }
//                .mapNotNull { ... }
//
//        mapperIgnoreAnnotatedClasses.forEach { processedProperties ->
//            generateAndWriteMapperIgnoreFile(
//                processedProperties,
//                codeGenerator,
//                logger,
//            )
//        }
        return emptyList()
    }

    /**
     * Retrieve Kotlin class declarations annotated with the [Mapper] annotation from the
     * specified [resolver].
     *
     * This function queries the [resolver] to find Kotlin class symbols that are annotated with the
     * [Mapper] annotation and returns them as a sequence of [KSClassDeclaration].
     *
     * @param resolver The resolver used to retrieve class declarations.
     * @return A sequence of [KSClassDeclaration] representing classes annotated with [Mapper].
     */
    private fun getDeclarationsAnnotatedWith(
        annotationClass: KClass<*>,
        resolver: Resolver
    ): Sequence<KSClassDeclaration> =
        resolver.getSymbolsWithAnnotation(annotationClass.java.name)
            .filterIsInstance<KSClassDeclaration>()
            .distinct()

    /**
     * Retrieve the target class type specified in a [KSAnnotation] with the [Mapper] annotation.
     *
     * This function extracts the target class type from the [KSAnnotation] associated with the [Mapper]
     * annotation, if it exists. It looks for the argument named [ARGUMENT_TARGET] within the annotation
     * and returns its value as a [KSType].
     *
     * @return The [KSType] representing the target class type, or null if not found.
     */
    private fun KSAnnotation.getTargetClassType(): KSType? {
        return this.arguments.firstOrNull {
            it.name?.asString() == ARGUMENT_TARGET
        }?.value as? KSType
    }

    /**
     * Retrieve the [KSAnnotation] associated with the [Mapper] annotation for a [KSClassDeclaration].
     *
     * This function searches for the [KSAnnotation] associated with the [Mapper] annotation within the
     * annotations of the specified [KSClassDeclaration].
     *
     * @return The [KSAnnotation] representing the [Mapper] annotation, or null if not found.
     */
    private fun KSClassDeclaration.getMapperAnnotation(): KSAnnotation? {
        return this.annotations.firstOrNull {
            it.shortName.asString() == ANNOTATION_MAPPER
        }
    }

    private fun KSClassDeclaration.getEditableFieldsFromAnnotation(): List<String>? {
        return this.annotations.firstOrNull { it.shortName.asString() == ANNOTATION_EDITABLE_MAPPER }
            ?.arguments
            ?.firstOrNull { it.name?.asString() == ARGUMENT_EDITABLE_FIELDS }
            ?.value as? List<String>
    }

    companion object {
        const val ARGUMENT_TARGET = "target"
        const val ANNOTATION_MAPPER = "Mapper"
        const val ANNOTATION_EDITABLE_MAPPER = "EditableMapper"
        const val ANNOTATION_MAPPER_IGNORE = "MapperIgnore"
        const val ARGUMENT_EDITABLE_FIELDS = "editableFields"
    }
}
