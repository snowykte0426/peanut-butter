package com.github.snowykte0426.peanut.butter.timezone

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.context.annotation.Import
import kotlin.reflect.full.findAnnotation

class EnableAutomaticTimeZoneTest : FunSpec({

    test("EnableAutomaticTimeZone annotation should be present on annotated class") {
        @EnableAutomaticTimeZone
        class TestApplication

        val annotation = TestApplication::class.findAnnotation<EnableAutomaticTimeZone>()
        annotation shouldNotBe null
    }

    test("EnableAutomaticTimeZone should have correct target and retention") {
        val annotationClass = EnableAutomaticTimeZone::class.java

        // Check if it's annotated with @Target
        val targetAnnotation = annotationClass.getAnnotation(Target::class.java)
        targetAnnotation shouldNotBe null
        targetAnnotation.allowedTargets shouldBe arrayOf(AnnotationTarget.CLASS)

        // Check if it's annotated with @Retention  
        val retentionAnnotation = annotationClass.getAnnotation(Retention::class.java)
        retentionAnnotation shouldNotBe null
        retentionAnnotation.value shouldBe AnnotationRetention.RUNTIME
    }

    test("EnableAutomaticTimeZone should be meta-annotated with @Import") {
        val annotationClass = EnableAutomaticTimeZone::class.java
        val importAnnotation = annotationClass.getAnnotation(Import::class.java)
        
        importAnnotation shouldNotBe null
        importAnnotation.value shouldBe arrayOf(TimeZoneAutoConfiguration::class)
    }

    test("EnableAutomaticTimeZone should work with Spring Boot application classes") {
        @EnableAutomaticTimeZone
        class SpringBootApplication

        @EnableAutomaticTimeZone  
        class TestConfig

        @EnableAutomaticTimeZone
        class MainApplication

        SpringBootApplication::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        TestConfig::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        MainApplication::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
    }

    test("EnableAutomaticTimeZone should be usable with other Spring annotations") {
        // Simulating a typical Spring Boot application setup
        @EnableAutomaticTimeZone
        class Application {
            // This would typically have @SpringBootApplication as well
        }

        @EnableAutomaticTimeZone
        class Configuration {
            // This would typically have @Configuration as well
        }

        Application::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        Configuration::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
    }

    test("EnableAutomaticTimeZone should import TimeZoneAutoConfiguration") {
        val annotationClass = EnableAutomaticTimeZone::class.java
        val importAnnotation = annotationClass.getAnnotation(Import::class.java)
        
        importAnnotation shouldNotBe null
        val importedClasses = importAnnotation.value
        importedClasses.size shouldBe 1
        importedClasses[0] shouldBe TimeZoneAutoConfiguration::class
    }

    test("annotation should be discoverable through reflection") {
        @EnableAutomaticTimeZone
        class ReflectionTestApp

        val clazz = ReflectionTestApp::class.java
        val annotations = clazz.annotations
        
        annotations.size shouldBe 1
        annotations[0].shouldBeInstanceOf<EnableAutomaticTimeZone>()
    }

    test("annotation should work with class inheritance") {
        @EnableAutomaticTimeZone
        abstract class BaseApplication

        class ConcreteApplication : BaseApplication()

        BaseApplication::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        // Note: Annotations are not inherited by default in Kotlin/Java
        ConcreteApplication::class.findAnnotation<EnableAutomaticTimeZone>() shouldBe null
    }

    test("multiple classes can use the annotation independently") {
        @EnableAutomaticTimeZone
        class App1

        @EnableAutomaticTimeZone
        class App2

        @EnableAutomaticTimeZone
        class App3

        App1::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        App2::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        App3::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
    }

    test("annotation should be a kotlin annotation") {
        val annotationClass = EnableAutomaticTimeZone::class
        
        // Verify it's a Kotlin annotation class
        annotationClass.isData shouldBe false
        annotationClass.isSealed shouldBe false
        annotationClass.isAbstract shouldBe true // Annotation classes are abstract
    }

    test("annotation class should have correct package") {
        val annotationClass = EnableAutomaticTimeZone::class.java
        val packageName = annotationClass.`package`.name
        
        packageName shouldBe "com.github.snowykte0426.peanut.butter.timezone"
    }

    test("annotation should support typical Spring Boot usage patterns") {
        // Pattern 1: Direct on main class
        @EnableAutomaticTimeZone
        class MainApp

        // Pattern 2: On configuration class
        @EnableAutomaticTimeZone
        class TimeZoneConfig

        // Pattern 3: Combined with other configurations
        @EnableAutomaticTimeZone
        class FullConfiguration

        MainApp::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        TimeZoneConfig::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        FullConfiguration::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
    }
})