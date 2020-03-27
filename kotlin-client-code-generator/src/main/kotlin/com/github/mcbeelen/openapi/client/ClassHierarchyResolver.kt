package com.github.mcbeelen.openapi.client

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.ComposedSchema
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema

class ClassHierarchyResolver(private val openAPI: OpenAPI) {

    private var classHierarchy = ClassHierarchy()

    fun analyze(): ClassHierarchy {

        openAPI.paths.forEach { path, pathItem -> processPath(path, pathItem) }
        return classHierarchy
    }

    private fun processPath(path: String, pathItem: PathItem) {
        val packageName : PackageName = determinePackage(path)
        if (!classHierarchy.packageMap.containsKey(packageName)) {
            classHierarchy = classHierarchy.withAdditionalPackage(packageName)
        }

        val operationsByTag = pathItem.readOperations().groupBy {
            FullyQualifiedName(packageName, TypeName((it.tags.first().toProperTypeName())))
        }
        operationsByTag.keys.forEach {
            println(it)
            pathItem.readOperationsMap().forEach { (method, operation) ->
                println("  ${method.name} : ${operation.operationId}")
                if (operation.requestBody != null) {
                    val content = operation.requestBody.content
                    content.forEach { typeName, mediaType ->
                        when (typeName) {
                            "multipart/form-data" -> println("    ${mediaType.schema.properties.keys}")
                            "application/json" -> processMediaType(mediaType, packageName)
                            else -> {
                                println("Unsupported MediaType: ${typeName}")
                            }
                        }
                    }
                }

                if (operation.responses != null) {
                    operation.responses.forEach { http, apiResponse ->
                        if (apiResponse.content != null) {
                            val content = apiResponse.content
                            content.forEach { typeName, mediaType ->
                                when (typeName) {
                                    "application/json" -> println("  ${http}   @Products(${mediaType?.schema?.`$ref`})")
                                    else -> {
                                        println("Unsupported MediaType: ${typeName}")
                                    }
                                }


                            }

                        }
                    }
                }

            }

        }


    }

    private fun processMediaType(mediaType: MediaType, packageName: PackageName) {
        val ref = mediaType.schema?.`$ref`
        if (ref != null) {
            classHierarchy = classHierarchy.withMappingOf(SchemaRef(ref), packageName)
            val type = ref.substringAfterLast("/")
            val schema = openAPI.components.schemas[type]

            if (schema != null) {
                when {
                    schema is ComposedSchema -> classHierarchy = processComposedSchema(classHierarchy, TypeName(type), schema)
                    schema.isEnumeration() -> classHierarchy = processEnumSchema(classHierarchy, TypeName(type), schema)
                }
            }
        }

    }

    private fun determinePackage(path: String) : PackageName = PackageName(path.substringAfter('/').substringBefore('/'))

    private fun processEnumSchema(classHierarchy: ClassHierarchy, typeName: TypeName, schema: Schema<Any>): ClassHierarchy {
        return classHierarchy.copy(enumerations = classHierarchy.enumerations.plus(typeName to schema))
    }

    private fun processComposedSchema(classHierarchy: ClassHierarchy, key: TypeName, composedSchema: ComposedSchema) : ClassHierarchy {
        return if (composedSchema.allOf.isNullOrEmpty()) {
            classHierarchy
        } else {
            val parentClassName = getParentClassName(composedSchema)
            classHierarchy
                    .copy(modelInterfaceClasses = classHierarchy.modelInterfaceClasses.plus(parentClassName))
                    .copy(childParentRelationships = classHierarchy.childParentRelationships.plus(key to parentClassName))
        }
    }

    private fun getParentClassName(composedSchema: ComposedSchema): TypeName {
        return TypeName(composedSchema.allOf.first().`$ref`.substringAfterLast("/"))
    }

}

/**
 * dummy-controller --> DummyController
 */
private fun String.toProperTypeName() = this.split("-").joinToString("") { it.capitalize() }


private fun <T> Schema<T>.isEnumeration() = this.enum != null && this.enum.isNotEmpty()


