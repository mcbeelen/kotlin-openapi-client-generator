package com.github.mcbeelen.openapi.client

import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.Schema

inline class PackageName(val value: String)
inline class TypeName(val value: String)
inline class SchemaRef(val value: String)

data class FullyQualifiedName(val packageName: PackageName, val typeName: TypeName) {
    override fun toString(): String {
        return "${packageName.value}.${typeName.value}"
    }
}

data class ClassHierarchy(
        val packageName: PackageName = PackageName(""),
        val packageMap: Map<PackageName, ClassHierarchy> = HashMap(),
        val operationInterFaceClasses: Map<FullyQualifiedName, PathItem> = HashMap(),
        val modelInterfaceClasses: Set<TypeName> = HashSet(),
        val enumerations: Map<TypeName, Schema<Any>> = HashMap(),
        val childParentRelationships: Map<TypeName, TypeName> = HashMap(),
        val typeToPackageMap: Map<SchemaRef, PackageName> = HashMap()
) {

    private val commonPackage = PackageName("common")

    fun withAdditionalPackage(packageName: PackageName) = this.copy(packageMap = packageMap + Pair(packageName, ClassHierarchy(packageName)))

    fun withMappingOf(schemaRef: SchemaRef, packageName: PackageName): ClassHierarchy {
        if (isNewType(schemaRef)) {
            return this.copy(typeToPackageMap = typeToPackageMap + Pair(schemaRef, packageName))
        }
        if (isAlreadyKnown(schemaRef, packageName)) {
            return this
        }
        return this.copy(typeToPackageMap = typeToPackageMap + Pair(schemaRef, commonPackage))


    }

    private fun isAlreadyKnown(schemaRef: SchemaRef, packageName: PackageName) =
            typeToPackageMap[schemaRef]!!.equals(packageName)

    private fun isNewType(schemaRef: SchemaRef) =
            !typeToPackageMap.containsKey(schemaRef)
}

