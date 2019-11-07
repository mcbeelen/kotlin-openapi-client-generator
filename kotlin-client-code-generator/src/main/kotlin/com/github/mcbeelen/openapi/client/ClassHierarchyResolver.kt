package com.github.mcbeelen.openapi.client

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.ComposedSchema

class ClassHierarchyResolver {
    fun analyze(openAPI: OpenAPI): ClassHierarchy {

        var classHierarchy = ClassHierarchy()

        openAPI.components.schemas.forEach { entry ->
            val schema = entry.value

            if (schema is ComposedSchema) {
                classHierarchy = processComposedSchema(classHierarchy, entry.key, schema)
            }
        }
        return classHierarchy

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
