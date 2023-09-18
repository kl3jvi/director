package com.kl3jvi.processor.mapper

import com.google.devtools.ksp.symbol.KSClassDeclaration

data class ProcessedProperties(
    val sourceClass: KSClassDeclaration,
    val targetClass: KSClassDeclaration?,
    val properties: List<ProcessedProperty>,
)
