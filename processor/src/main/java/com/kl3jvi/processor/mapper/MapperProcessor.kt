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
        processMapper(resolver, propertyProcessor)
        processEditableMapper(resolver, propertyProcessor)
        return emptyList()
    }

    private fun processEditableMapper(
        resolver: Resolver,
        propertyProcessor: ClassPropertyProcessor,
    ) {
        val editableMapperAnnotatedClasses =
            getDeclarationsAnnotatedWith(EditableMapper::class, resolver) { classDeclaration ->
                val editableFields = classDeclaration.getEditableFieldsFromAnnotation()

                if (editableFields.isEmpty()) {
                    logger.error("Could not extract editable fields for ${classDeclaration.qualifiedName?.asString()}")
                    return@getDeclarationsAnnotatedWith null
                }
                val targetClassType = classDeclaration.getEditableMapperAnnotation()
                    ?.getTargetClassType()
                    ?.firstOrNull()
                    ?: return@getDeclarationsAnnotatedWith null

                val targetClassName = targetClassType
                    .declaration
                    .qualifiedName
                    ?.asString()
                    ?: return@getDeclarationsAnnotatedWith null

                propertyProcessor.processClassForEditableMapper(
                    classDeclaration,
                    targetClassName,
                    editableFields,
                    resolver,
                    logger,
                )
            }

        editableMapperAnnotatedClasses.forEach { processedProperties ->
            generateAndWriteEditableMapperFile(
                processedProperties,
                codeGenerator,
                logger,
            )
        }
    }

    private fun processMapper(resolver: Resolver, propertyProcessor: ClassPropertyProcessor) {
        val annotatedClasses =
            getDeclarationsAnnotatedWith(Mapper::class, resolver) { classDeclaration ->
                val targetClassType = classDeclaration.getMapperAnnotation()
                    ?.getTargetClassType()
                    ?: return@getDeclarationsAnnotatedWith null

                val targetClassName = targetClassType
                    .mapNotNull {
                        it.declaration
                            .qualifiedName
                            ?.asString() ?: return@mapNotNull null
                    }

                propertyProcessor.processClass(
                    classDeclaration,
                    targetClassName,
                    resolver,
                )
            }

        annotatedClasses.forEach { processedProperties ->
            generateAndWriteMapperFile(
                processedProperties,
                codeGenerator,
                logger,
            )
        }
    }

    /**
     * Retrieves a sequence of declarations annotated with the given annotation class.
     *
     * This function searches for Kotlin symbols that have been annotated with the specified annotation
     * and applies a transformation to them before returning a sequence.
     *
     * @param annotationClass The `KClass` of the annotation to search for.
     * @param resolver The resolver used to find symbols with the given annotation.
     * @param transform A transformation function that is applied to each `KSClassDeclaration` found.
     *                  It should return an instance of type `R` or null if the transformation isn't applicable.
     *
     * @return A sequence of transformed elements of type `R` corresponding to the symbols that were
     *         annotated with the given annotation.
     */
    private fun <R : Any> getDeclarationsAnnotatedWith(
        annotationClass: KClass<*>,
        resolver: Resolver,
        transform: (KSClassDeclaration) -> R?,
    ): Sequence<R> {
        return resolver.getSymbolsWithAnnotation(annotationClass.java.name)
            .filterIsInstance<KSClassDeclaration>()
            .distinct()
            .filter { it.validate() && it.validateModifierContainsMapper(logger) }
            .mapNotNull(transform)
    }

    /**
     * Retrieve the target class type specified in a [KSAnnotation] with the [Mapper] annotation.
     *
     * This function extracts the target class type from the [KSAnnotation] associated with the [Mapper]
     * annotation, if it exists. It looks for the argument named [ARGUMENT_TARGET] within the annotation
     * and returns its value as a [KSType].
     *
     * @return The [KSType] representing the target class type, or null if not found.
     */
    private fun KSAnnotation.getTargetClassType(): List<KSType>? {
        val argumentValue = this.arguments.firstOrNull {
            it.name?.asString() == ARGUMENT_TARGET
        }?.value

        return when {
            argumentValue is List<*> && argumentValue.all { it is KSType } -> argumentValue as? List<KSType>
                ?: emptyList()
            else -> listOf(argumentValue) as? List<KSType> ?: emptyList()
        }
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
            it.shortName.asString() == Mapper::class.simpleName
        }
    }

    private fun KSClassDeclaration.getEditableMapperAnnotation(): KSAnnotation? {
        return this.annotations.firstOrNull {
            it.shortName.asString() == EditableMapper::class.simpleName
        }
    }

    private fun KSClassDeclaration.getEditableFieldsFromAnnotation(): List<String> {
        return this.annotations.firstOrNull { it.shortName.asString() == EditableMapper::class.simpleName }
            ?.arguments
            ?.firstOrNull { it.name?.asString() == ARGUMENT_EDITABLE_FIELDS }
            ?.value as? List<String> ?: emptyList()
    }

    companion object {
        const val ARGUMENT_TARGET = "target"
        const val ARGUMENT_EDITABLE_FIELDS = "editableFields"
    }
}
