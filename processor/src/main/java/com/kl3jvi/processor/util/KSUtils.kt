package com.kl3jvi.processor.util

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName

fun KSClassDeclaration.asTypeName(): ClassName {
    return ClassName(this.packageName.asString(), this.simpleName.asString())
}
