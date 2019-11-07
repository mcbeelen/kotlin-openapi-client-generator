package com.github.mcbeelen.openapi.client

import com.natpryce.hamkrest.anyElement
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import org.junit.Test
import java.io.IOException

class ClassHierarchyResolverTest {

    @Test
    @Throws(IOException::class)
    fun itShouldFindEnumerations() {

        val openAPI = readJsonFrom("src/test/resources/input_example.json")

        val resolver = ClassHierarchyResolver()
        val classHierarchy = resolver.analyze(openAPI)

        assertThat(classHierarchy.enumerations.keys, hasSize(equalTo(1)))
        assertThat(classHierarchy.enumerations.keys, anyElement(equalTo("CarType")))
    }


    @Test
    @Throws(IOException::class)
    fun itShouldFindProductAsParentClass() {

        val openAPI = readJsonFrom("src/test/resources/input_example.json")

        val resolver = ClassHierarchyResolver()
        val classHierarchy = resolver.analyze(openAPI)

        assertThat(classHierarchy.interfaceClasses, hasSize(equalTo(1)))
        assertThat(classHierarchy.interfaceClasses, anyElement(equalTo("Product")))
    }

    @Test
    @Throws(IOException::class)
    fun itShouldFindCarAndLaptopAsChildClassesOfProduct() {

        val openAPI = readJsonFrom("src/test/resources/input_example.json")

        val resolver = ClassHierarchyResolver()
        val classHierarchy = resolver.analyze(openAPI)

//        val foundChildParentRelationships = classHierarchy.getChildParentRelationships()
//        assertThat(foundChildParentRelationships.keys, hasSize(2))
//        assertThat(foundChildParentRelationships.keys, hasElement("Car"))
//        assertThat(foundChildParentRelationships.keys, hasElement("Laptop"))
//        assertThat(foundChildParentRelationships.get("Car"), equalTo("Product"))
    }
}