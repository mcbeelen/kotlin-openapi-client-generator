package com.github.mcbeelen.openapi.client

import io.swagger.v3.oas.models.media.Schema

data class ClassHierarchy(
    val interfaceClasses: Set<String> = HashSet(),
    val enumerations: Map<String, Schema<Any>> = HashMap(),
    val childParentRelationships: Map<String, String> = HashMap()
)

