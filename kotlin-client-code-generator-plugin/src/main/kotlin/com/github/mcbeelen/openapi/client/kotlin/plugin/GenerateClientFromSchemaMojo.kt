package com.github.mcbeelen.openapi.client.kotlin.plugin

import com.github.mcbeelen.openapi.client.kotlin.OpenApiClientCodeGenerator
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.Paths.get

const val EMPTY = ""

@Mojo(name = "generateClientFromOpenApi", defaultPhase = GENERATE_SOURCES)
class GenerateClientFromSchemaMojo : AbstractMojo() {

    @Parameter(required = true)
    private var outputPackage: String = EMPTY

    @Parameter(required = true)
    private var outputPath: String = EMPTY

    @Parameter(required = true)
    private var schemaPath: String = EMPTY


    @Parameter(defaultValue = "\${project}")
    private var mavenProject: MavenProject? = null


    override fun execute() {

        val project = checkNotNull(mavenProject) { "var project: MavenProject should have been initialized and set" }

        val openApiClientCodeGenerator = OpenApiClientCodeGenerator(get(schemaPath), get(outputPath), outputPackage)
                .generateClientCode()

        project.addCompileSourceRoot(outputPath)
    }




}