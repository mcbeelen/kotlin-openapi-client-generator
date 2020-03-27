package com.github.mcbeelen.openapi.client.kotlin

import com.github.mcbeelen.openapi.client.ClassHierarchy
import com.github.mcbeelen.openapi.client.ClassHierarchyResolver
import com.github.mcbeelen.openapi.client.TypeName
import com.squareup.kotlinpoet.KModifier.PUBLIC
import com.squareup.kotlinpoet.TypeSpec
import io.swagger.v3.oas.integration.IntegrationObjectMapperFactory
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.media.Schema
import java.nio.file.Path

class OpenApiClientCodeGenerator(
        private val schemaPath: Path,
        outputPath: Path,
        outputPackage: String) {

    private val kotlinFileWriter = KotlinFileWriter(outputPath, outputPackage)

    fun generateClientCode() {

        val openAPI = readSpecificationFromJson()

        val classHierarchy = ClassHierarchyResolver(openAPI).analyze()

        generateKotlinFilesForAllClasses(classHierarchy)

    }

    private fun generateKotlinFilesForAllClasses(classHierarchy: ClassHierarchy) {
        generateEnums(classHierarchy)
    }

    private fun generateEnums(classHierarchy: ClassHierarchy) {
        val enumClasses = classHierarchy.enumerations
        enumClasses.forEach { (key, schema) -> kotlinFileWriter.writeKtFileFor(generateEnumClass(key, schema)) }
    }

    private fun generateEnumClass(typeName: TypeName, schema: Schema<*>): TypeSpec {
        val enumBuilder = TypeSpec.enumBuilder(typeName.value).addModifiers(PUBLIC)
        schema.enum.forEach { enumBuilder.addEnumConstant(it.toString()) }
        return enumBuilder.build()
    }


    private fun readSpecificationFromJson() =
            IntegrationObjectMapperFactory
                    .createJson()
                    .readValue<OpenAPI>(schemaPath.toFile(), OpenAPI::class.java)


}
