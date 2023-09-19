package com.kl3jvi.processor.property

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getDeclaredProperties
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
        editableFields: List<String>,
        resolver: Resolver,
    ): ProcessedProperties {
        // Retrieve target class from annotation
        val targetClass = resolver.getClassDeclarationByName("com.kl3jvi.api.EditableMapper")

        // Retrieve all properties of the classDeclaration and targetClass
        val sourceProperties = classDeclaration.getAllProperties()
        val targetProperties =
            targetClass?.getAllProperties()?.associateBy { it.simpleName.asString() }

        // Filter the source properties by the ones that are listed in editableFields
        val filteredSourceProperties =
            sourceProperties.filter { it.simpleName.asString() in editableFields }

        // Map these properties to ProcessedProperty objects
        val processedPropertiesList = filteredSourceProperties.map { sourceProperty ->
            val sourceName = sourceProperty.simpleName.asString()
            val sourceType = sourceProperty.type.resolve().toString()

            val targetProperty = targetProperties?.get(sourceName)
            val targetName = targetProperty?.simpleName?.asString()
            val targetType = targetProperty?.type?.resolve()?.toString()

            val isMatching = targetName != null && sourceType == targetType

            ProcessedProperty(sourceName, sourceType, targetName, targetType, isMatching)
        }.toList()

        // Return the processed properties
        return ProcessedProperties(classDeclaration, targetClass, processedPropertiesList)
    }
}
