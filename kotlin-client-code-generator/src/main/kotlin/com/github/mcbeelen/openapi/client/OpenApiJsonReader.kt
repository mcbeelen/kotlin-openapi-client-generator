package com.github.mcbeelen.openapi.client

import io.swagger.v3.oas.integration.IntegrationObjectMapperFactory
import io.swagger.v3.oas.models.OpenAPI
import java.io.File
import java.io.IOException

@Throws(IOException::class)
fun readJsonFrom(openApiSchemaPath: String): OpenAPI {
    return IntegrationObjectMapperFactory.createJson().readValue(File(openApiSchemaPath), OpenAPI::class.java)
}
