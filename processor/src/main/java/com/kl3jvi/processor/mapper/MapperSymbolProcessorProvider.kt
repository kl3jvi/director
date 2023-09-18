package com.kl3jvi.processor.mapper

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

public class MapperSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return MapperProcessor(
            environment.codeGenerator,
            environment.logger,
        )
    }
}
