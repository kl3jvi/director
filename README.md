# Director Library
[<img src="https://raw.githubusercontent.com/kl3jvi/mappy/764dd6626852d563e21472f4cef30c640a188ff1/assets/mappy.svg">](DirectorBanner)

The `Director` library is an annotation-based code generation tool that helps developers automatically generate mapping functions for their Kotlin classes.

## Features

- Annotation-based: Simply annotate your data classes and let the library do the rest.
- Automatic generation: No need to manually write mapping functions.
- Editable mapper support: Easily define which fields are editable and which are not.
- Clear and concise mapping: Generated functions are readable and easy to understand.

## Installation

[Installation Instructions Placeholder - Typically this would be how to add the library to Gradle/Maven]

## How to Use

1. **Annotate your classes**

   Use the `@Mapper` and `@EditableMapper` annotations to specify your source and target classes.

   ```kotlin
   @Mapper(target = TargetClass::class)
   data class SourceClass(val prop1: Type1, val prop2: Type2)
   ```
2. **Use Generated Mappers:**

   After building your project, Director will generate extension functions that you can use for mapping:

   ```kotlin
   val source = SourceClass(...)
   val target = source.toTargetClass()
   ```
3. **Editable Fields:**
   
    Use the `@EditableMapper` to specify fields that are editable:

   ```kotlin
   @EditableMapper(target = TargetClass::class, editableFields = ["field1", "field2"])
   data class SourceClass(...)
   ```
