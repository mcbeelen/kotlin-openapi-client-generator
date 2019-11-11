package com.github.mcbeelen.openapi.client.kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.file.Path

class KotlinFileWriter(val outputPath: Path, val outputPackage: String) {

    fun writeKtFileFor(typeSpec: TypeSpec) =
            FileSpec.builder(outputPackage, getFileName(typeSpec))
                    .addType(typeSpec)
                    .build()
                    .writeTo(outputPath)


    private fun getFileName(typeSpec: TypeSpec) = checkNotNull(typeSpec.name)

}