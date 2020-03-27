package com.github.mcbeelen.openapi.client

import com.natpryce.hamkrest.anyElement
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.hasSize
import org.junit.Test
import java.io.IOException

class ClassHierarchyResolverTest {

    @Test
    @Throws(IOException::class)
    fun itShouldFindTwoPackages() {

        val openAPI = readJsonFrom("src/test/resources/input_example.json")

        val resolver = ClassHierarchyResolver(openAPI)
        val classHierarchy = resolver.analyze()

        assertThat(classHierarchy.packageMap.keys, hasSize(equalTo(2)))
        assertThat(classHierarchy.packageMap.keys, anyElement(equalTo(PackageName("cars"))))
        assertThat(classHierarchy.packageMap.keys, anyElement(equalTo(PackageName("dummy"))))
    }


    @Test
    @Throws(IOException::class)
    fun carTypeEnumShouldBeGeneratedInCarPackage() {

        val openAPI = readJsonFrom("src/test/resources/input_example.json")

        val resolver = ClassHierarchyResolver(openAPI)
        val classHierarchy = resolver.analyze()

        val carEnumerations = (classHierarchy.packageMap[PackageName("cars")] ?: error("Package 'cars' not found")).enumerations

        assertThat(carEnumerations.keys, anyElement(equalTo(TypeName("CarType"))))
    }


    @Test
    @Throws(IOException::class)
    fun itShouldFindProductAsParentClass() {

        val openAPI = readJsonFrom("src/test/resources/input_example.json")

        val resolver = ClassHierarchyResolver(openAPI)
        val classHierarchy = resolver.analyze()

        assertThat(classHierarchy.modelInterfaceClasses, hasSize(equalTo(1)))
        assertThat(classHierarchy.modelInterfaceClasses, anyElement(equalTo(TypeName("Product"))))
    }

    @Test
    @Throws(IOException::class)
    fun itShouldFindCarAndLaptopAsChildClassesOfProduct() {

        val openAPI = readJsonFrom("src/test/resources/input_example.json")

        val resolver = ClassHierarchyResolver(openAPI)
        val classHierarchy = resolver.analyze()

        val foundChildParentRelationships = classHierarchy.childParentRelationships
        assertThat(foundChildParentRelationships.keys, hasSize(equalTo(2)))
        assertThat(foundChildParentRelationships.keys, hasElement(TypeName("Car")))
        assertThat(foundChildParentRelationships.keys, hasElement(TypeName("Laptop")))
        assertThat(foundChildParentRelationships[TypeName("Car")], equalTo(TypeName("Product")))
    }
}