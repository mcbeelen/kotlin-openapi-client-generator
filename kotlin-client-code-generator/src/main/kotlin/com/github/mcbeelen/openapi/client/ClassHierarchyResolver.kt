package com.github.mcbeelen.openapi.client

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.Schema

class ClassHierarchyResolver {
    fun analyze(openAPI: OpenAPI): ClassHierarchy {

        var classHierarchy = ClassHierarchy()

        openAPI.components.schemas.forEach { entry ->
            val schema = entry.value

            when {
                schema is ComposedSchema -> classHierarchy = processComposedSchema(classHierarchy, entry.key, schema)
                schema.isEnumeration() -> classHierarchy = processEnumSchema(classHierarchy, entry.key, schema)
            }
        }
        return classHierarchy

    }

    private fun processEnumSchema(classHierarchy: ClassHierarchy, key: String, schema: Schema<Any>): ClassHierarchy {
        return classHierarchy.copy(enumerations = classHierarchy.enumerations.plus(Pair(key, schema)))
    }

    private fun processComposedSchema(classHierarchy: ClassHierarchy, key: String, composedSchema: ComposedSchema) : ClassHierarchy {
        if (composedSchema.allOf.isNullOrEmpty()) {
            return classHierarchy
        } else {
            return classHierarchy.copy(interfaceClasses = classHierarchy.interfaceClasses.plus(getParentClassName(composedSchema)))
        }
    }

    private fun getParentClassName(composedSchema: ComposedSchema): String {
        return composedSchema.allOf.first().`$ref`.substringAfterLast("/")
    }

}

private fun <T> Schema<T>.isEnumeration() = this.enum != null && ! this.enum.isEmpty()


