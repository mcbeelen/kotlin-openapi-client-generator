package com.github.mcbeelen.openapi.client.kotlin

import com.natpryce.hamkrest.anyElement
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.contains
import org.apache.commons.io.filefilter.NameFileFilter
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Paths


class OpenApiClientCodeGeneratorTest {

    @Rule @JvmField
    public var tempFolder = TemporaryFolder()

    @Test
    fun `it should write enum classes for Enumerations`() {
        val resource = OpenApiClientCodeGeneratorTest::class.java.getResource("/input_example.json")
        val schemaPath = Paths.get(resource.toURI())
        val outputPath = tempFolder.root.toPath()
        val outputPackage = "tld.organization.project"

        val openApiClientCodeGenerator = OpenApiClientCodeGenerator(schemaPath, outputPath, outputPackage)
                .generateClientCode()

        val toTypedArray = outputPackage.split('.').toTypedArray()
        val basePath = Paths.get(tempFolder.root.path, *toTypedArray)


        val directoryForGeneratedKtFiles = basePath.toFile()
        directoryForGeneratedKtFiles.list()
        val generatedFiles = directoryForGeneratedKtFiles.list(NameFileFilter("CarType.kt"))
        checkNotNull(generatedFiles) { "Generated files should be readable" }

        val readLines = File(directoryForGeneratedKtFiles, generatedFiles[0]).readLines()
        assertThat(readLines, anyElement(contains(Regex("VAN"))))

    }
}