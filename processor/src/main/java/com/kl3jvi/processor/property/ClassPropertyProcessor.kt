package com.kl3jvi.processor.property

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.kl3jvi.processor.mapper.ProcessedProperties
import com.kl3jvi.processor.mapper.ProcessedProperty

class ClassPropertyProcessor {

    /**
     * Process properties of a source class and generate a list of processed properties.
     *
     * This function analyzes the properties of the given [sourceClass], compares them with the
     * properties of a target class specified by [targetClassName], and generates a list of
     * [ProcessedProperty] objects that describe the mapping between the source and target properties.
     *
     * @param sourceClass The source class to analyze.
     * @param targetClassName The name of the target class to compare with.
     * @param resolver The resolver used to retrieve the target class declaration.
     * @return A [ProcessedProperties] object containing the source class, target class, and the
     *         list of processed properties, or `null` if the target class is not found.
     */
    fun processClass(
        sourceClass: KSClassDeclaration,
        targetClassNames: List<String>,
        resolver: Resolver,
    ): List<ProcessedProperties?> {
        return targetClassNames.map { targetClassName ->
            val targetClass = resolver.getClassDeclarationByName(targetClassName) ?: return@map null
            val sourceProperties = sourceClass.getDeclaredProperties()
            val targetProperties = targetClass.getDeclaredProperties()

            val processedProperties = sourceProperties.map { sourceProp ->
                val correspondingTargetProperty =
                    targetProperties.find { it.simpleName.asString() == sourceProp.simpleName.asString() }

                ProcessedProperty(
                    sourceName = sourceProp.simpleName.asString(),
                    sourceType = sourceProp.type.resolve().declaration.qualifiedName?.asString()
                        ?: "",
                    targetName = correspondingTargetProperty?.simpleName?.asString(),
                    targetType = correspondingTargetProperty?.type?.resolve()?.declaration?.qualifiedName?.asString(),
                    isMatching = (sourceProp.type.resolve().declaration == correspondingTargetProperty?.type?.resolve()?.declaration),
                )
            }.toList()

            ProcessedProperties(sourceClass, targetClass, processedProperties)
        }
    }

    fun processClassForEditableMapper(
        classDeclaration: KSClassDeclaration,
        targetClassName: String,
        editableFields: List<String>,
        resolver: Resolver,
        logger: KSPLogger,
    ): ProcessedProperties {
        val targetClass = resolver.getClassDeclarationByName(targetClassName)

        val sourceProperties =
            classDeclaration.getAllProperties().associateBy { it.simpleName.asString() }
        val targetProperties =
            targetClass?.getAllProperties()?.associateBy { it.simpleName.asString() }

        // Check if all editableFields are valid properties of sourceClass
        editableFields.forEach { field ->
            if (!sourceProperties.containsKey(field)) {
                logger.error("Field $field is not a valid property of ${classDeclaration.qualifiedName?.asString()}")
            }
        }

        val processedPropertiesList = editableFields.mapNotNull { editableField ->
            val sourceProperty = sourceProperties[editableField]
            val targetProperty = targetProperties?.get(editableField)

            if (sourceProperty != null && targetProperty != null) {
                val sourceType = sourceProperty.type.resolve().toString()
                val targetType = targetProperty.type.resolve().toString()

                val isMatching = sourceType == targetType

                // Add logic here for type transformation if needed

                ProcessedProperty(editableField, sourceType, editableField, targetType, isMatching)
            } else {
                // If either source or target property doesn't exist for an editableField, return null.
                // This can be enhanced to handle fallbacks or default values.
                null
            }
        }

        return ProcessedProperties(classDeclaration, targetClass, processedPropertiesList)
    }
}

