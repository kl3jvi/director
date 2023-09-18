package com.kl3jvi.processor.mapper

data class ProcessedProperty(
    val sourceName: String,
    val sourceType: String,
    val targetName: String?,
    val targetType: String?,
    val isMatching: Boolean,
)
