package com.github.snowykte0426.peanut.butter.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.slf4j.LoggerFactory

class PerformanceLoggingTest : FunSpec({

    lateinit var listAppender: ListAppender<ILoggingEvent>
    lateinit var testLogger: ch.qos.logback.classic.Logger

    beforeEach {
        testLogger = LoggerFactory.getLogger(PerformanceLoggingTest::class.java) as ch.qos.logback.classic.Logger
        listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        testLogger.addAppender(listAppender)
        testLogger.level = Level.TRACE
    }

    afterEach {
        testLogger.detachAppender(listAppender)
    }

    test("logExecutionTime should measure and log execution time") {
        val result = logExecutionTime("Test operation") {
            Thread.sleep(10)
            42
        }

        result shouldBe 42
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldContain "Test operation completed in"
        logEvents[0].message shouldContain "ms"
    }

    test("logExecutionTime with custom logger should use provided logger") {
        val customLogger = LoggerFactory.getLogger("CustomLogger") as ch.qos.logback.classic.Logger
        val customAppender = ListAppender<ILoggingEvent>()
        customAppender.start()
        customLogger.addAppender(customAppender)
        customLogger.level = Level.DEBUG

        val result = logExecutionTime(customLogger, "Custom operation") {
            "test result"
        }

        result shouldBe "test result"
        customAppender.list.size shouldBe 1
        customAppender.list[0].message shouldContain "Custom operation completed in"
        
        customLogger.detachAppender(customAppender)
    }

    test("logExecutionTime should handle exceptions and still log time") {
        var exceptionThrown = false
        
        try {
            logExecutionTime("Failing operation") {
                throw RuntimeException("Test exception")
            }
        } catch (e: RuntimeException) {
            exceptionThrown = true
        }

        exceptionThrown shouldBe true
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].message shouldContain "Failing operation completed in"
    }

    test("logMethodExecution should log method entry and successful exit") {
        val result = logMethodExecution("testMethod") {
            "method result"
        }

        result shouldBe "method result"
        val logEvents = listAppender.list
        logEvents.size shouldBe 2
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldBe "Entering method: testMethod"
        logEvents[1].level shouldBe Level.DEBUG
        logEvents[1].message shouldBe "Exiting method: testMethod successfully"
    }

    test("logMethodExecution should log method entry and exit with parameters") {
        val params = mapOf("userId" to 123, "name" to "testUser")
        
        val result = logMethodExecution("processUser", params) {
            "processing complete"
        }

        result shouldBe "processing complete"
        val logEvents = listAppender.list
        logEvents.size shouldBe 2
        logEvents[0].message shouldContain "Entering method: processUser with params:"
        logEvents[0].message shouldContain "userId=123"
        logEvents[0].message shouldContain "name=testUser"
        logEvents[1].message shouldBe "Exiting method: processUser successfully"
    }

    test("logMethodExecution should log method entry and error exit") {
        var exceptionThrown = false
        
        try {
            logMethodExecution("failingMethod") {
                throw IllegalStateException("Method failed")
            }
        } catch (e: IllegalStateException) {
            exceptionThrown = true
        }

        exceptionThrown shouldBe true
        val logEvents = listAppender.list
        logEvents.size shouldBe 2
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldBe "Entering method: failingMethod"
        logEvents[1].level shouldBe Level.ERROR
        logEvents[1].message shouldBe "Exiting method: failingMethod with error: Method failed"
        logEvents[1].throwableProxy shouldNotBe null
    }

    test("logPerformance should measure time and memory usage") {
        val result = logPerformance("Memory test operation") {
            // Create some objects to use memory
            val list = mutableListOf<String>()
            repeat(1000) { list.add("test$it") }
            list.size
        }

        result shouldBe 1000
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldContain "Memory test operation - Time:"
        logEvents[0].message shouldContain "ms, Memory used:"
        logEvents[0].message shouldContain "KB"
    }

    test("logPerformance should handle negative memory usage") {
        val result = logPerformance("GC test operation") {
            System.gc()
            "result"
        }

        result shouldBe "result"
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldContain "GC test operation - Time:"
    }

    test("logOnException should return result when no exception occurs") {
        val result = logOnException("Safe operation") {
            "successful result"
        }

        result shouldBe "successful result"
        val logEvents = listAppender.list
        logEvents.size shouldBe 0
    }

    test("logOnException should return null and log error when exception occurs") {
        val result = logOnException("Unsafe operation") {
            throw RuntimeException("Something went wrong")
        }

        result shouldBe null
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.ERROR
        logEvents[0].message shouldBe "Exception in operation 'Unsafe operation': Something went wrong"
        logEvents[0].throwableProxy?.message shouldBe "Something went wrong"
    }

    test("logWarningOnException should return result when no exception occurs") {
        val result = logWarningOnException("Safe operation", "default") {
            "successful result"
        }

        result shouldBe "successful result"
        val logEvents = listAppender.list
        logEvents.size shouldBe 0
    }

    test("logWarningOnException should return default value and log warning when exception occurs") {
        val result = logWarningOnException("Unsafe operation", "fallback value") {
            throw IllegalArgumentException("Invalid input")
        }

        result shouldBe "fallback value"
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.WARN
        logEvents[0].message shouldBe "Warning in operation 'Unsafe operation': Invalid input, returning default value"
        logEvents[0].throwableProxy?.message shouldBe "Invalid input"
    }

    test("logWarningOnException should handle different return types") {
        val numberResult = logWarningOnException("Number operation", 0) {
            throw Exception("Number error")
        }

        val booleanResult = logWarningOnException("Boolean operation", false) {
            throw Exception("Boolean error")
        }

        numberResult shouldBe 0
        booleanResult shouldBe false
        
        val logEvents = listAppender.list
        logEvents.size shouldBe 2
        logEvents[0].message shouldContain "Number operation"
        logEvents[1].message shouldContain "Boolean operation"
    }

    test("performance functions should work with different object types") {
        val stringObj = "test"
        val listObj = listOf(1, 2, 3)
        
        val result1 = stringObj.logExecutionTime("String operation") { "string result" }
        val result2 = listObj.logMethodExecution("List method") { "list result" }

        result1 shouldBe "string result"
        result2 shouldBe "list result"
        
        val logEvents = listAppender.list
        logEvents.size shouldBe 3
    }

    test("logExecutionTime should work with complex return types") {
        data class ComplexResult(val value: String, val count: Int)
        
        val result = logExecutionTime("Complex operation") {
            ComplexResult("test", 42)
        }

        result.value shouldBe "test"
        result.count shouldBe 42
        
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].message shouldContain "Complex operation completed in"
    }
})