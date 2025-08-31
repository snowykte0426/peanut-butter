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

        val targetAnnotation = annotationClass.getAnnotation(Target::class.java)
        targetAnnotation shouldNotBe null
        targetAnnotation.allowedTargets shouldBe arrayOf(AnnotationTarget.CLASS)

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
        @EnableAutomaticTimeZone
        class Application {
        }

        @EnableAutomaticTimeZone
        class Configuration {
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

        val enableTimeZoneAnnotation = annotations.find { it is EnableAutomaticTimeZone }
        enableTimeZoneAnnotation shouldNotBe null
        enableTimeZoneAnnotation.shouldBeInstanceOf<EnableAutomaticTimeZone>()
    }

    test("annotation should work with class inheritance") {
        @EnableAutomaticTimeZone
        abstract class BaseApplication

        class ConcreteApplication : BaseApplication()

        BaseApplication::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
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

        annotationClass.isData shouldBe false
        annotationClass.isSealed shouldBe false
        annotationClass.isAbstract shouldBe false
    }

    test("annotation class should have correct package") {
        val annotationClass = EnableAutomaticTimeZone::class.java
        val packageName = annotationClass.`package`.name
        
        packageName shouldBe "com.github.snowykte0426.peanut.butter.timezone"
    }

    test("annotation should support typical Spring Boot usage patterns") {
        @EnableAutomaticTimeZone
        class MainApp

        @EnableAutomaticTimeZone
        class TimeZoneConfig

        @EnableAutomaticTimeZone
        class FullConfiguration

        MainApp::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        TimeZoneConfig::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
        FullConfiguration::class.findAnnotation<EnableAutomaticTimeZone>() shouldNotBe null
    }
})