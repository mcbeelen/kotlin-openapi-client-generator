package com.github.mcbeelen.openapi.client


import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import org.junit.Test
import java.io.IOException
import java.util.Objects

class OpenApiJsonReaderTest {

    val isNotNull = Matcher(Objects::nonNull)

    @Test
    @Throws(IOException::class)
    fun itShouldBeAbleToExtractOpenApiSpecificationFromJson() {

        val openAPI = readJsonFrom("src/test/resources/input_example.json")

        assertThat(openAPI, isNotNull)
        assertThat(openAPI.getComponents().getSchemas().keys, hasSize(equalTo(14)))

    }


}