package com.github.snowykte0426.peanut.butter.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.slf4j.LoggerFactory

class ConvenientLoggingTest : FunSpec({

    lateinit var listAppender: ListAppender<ILoggingEvent>
    lateinit var testLogger: ch.qos.logback.classic.Logger

    beforeEach {
        val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as ch.qos.logback.classic.Logger
        testLogger = rootLogger
        
        listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()
        testLogger.addAppender(listAppender)
        testLogger.level = Level.TRACE
    }

    afterEach {
        testLogger.detachAppender(listAppender)
    }

    test("logDebug should log debug message") {
        logDebug("Debug message")

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldBe "Debug message"
    }

    test("logDebug with arguments should log debug message with substitutions") {
        logDebug("Debug message with {} and {}", "arg1", "arg2")

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldBe "Debug message with {} and {}"
        logEvents[0].argumentArray shouldBe arrayOf("arg1", "arg2")
    }

    test("logDebug with throwable should log debug message with exception") {
        val exception = RuntimeException("Test exception")
        logDebug("Debug with exception", exception)

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldBe "Debug with exception"
        logEvents[0].throwableProxy?.message shouldBe "Test exception"
    }

    test("logInfo should log info message") {
        logInfo("Info message")

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.INFO
        logEvents[0].message shouldBe "Info message"
    }

    test("logInfo with arguments should log info message with substitutions") {
        logInfo("Info message with {}", "argument")

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.INFO
        logEvents[0].message shouldBe "Info message with {}"
        logEvents[0].argumentArray shouldBe arrayOf("argument")
    }

    test("logInfo with throwable should log info message with exception") {
        val exception = IllegalStateException("Test exception")
        logInfo("Info with exception", exception)

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.INFO
        logEvents[0].message shouldBe "Info with exception"
        logEvents[0].throwableProxy?.message shouldBe "Test exception"
    }

    test("logWarn should log warn message") {
        logWarn("Warning message")

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.WARN
        logEvents[0].message shouldBe "Warning message"
    }

    test("logWarn with arguments should log warn message with substitutions") {
        logWarn("Warning with {} arguments", 2)

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.WARN
        logEvents[0].message shouldBe "Warning with {} arguments"
        logEvents[0].argumentArray shouldBe arrayOf(2)
    }

    test("logWarn with throwable should log warn message with exception") {
        val exception = IllegalArgumentException("Invalid argument")
        logWarn("Warning with exception", exception)

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.WARN
        logEvents[0].message shouldBe "Warning with exception"
        logEvents[0].throwableProxy?.message shouldBe "Invalid argument"
    }

    test("logError should log error message") {
        logError("Error message")

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.ERROR
        logEvents[0].message shouldBe "Error message"
    }

    test("logError with arguments should log error message with substitutions") {
        logError("Error occurred at {} with code {}", "service", 500)

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.ERROR
        logEvents[0].message shouldBe "Error occurred at {} with code {}"
        logEvents[0].argumentArray shouldBe arrayOf("service", 500)
    }

    test("logError with throwable should log error message with exception") {
        val exception = RuntimeException("Critical error")
        logError("Error with exception", exception)

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.ERROR
        logEvents[0].message shouldBe "Error with exception"
        logEvents[0].throwableProxy?.message shouldBe "Critical error"
    }

    test("logTrace should log trace message") {
        logTrace("Trace message")

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.TRACE
        logEvents[0].message shouldBe "Trace message"
    }

    test("logTrace with arguments should log trace message with substitutions") {
        logTrace("Tracing method {} with parameter {}", "testMethod", "testParam")

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.TRACE
        logEvents[0].message shouldBe "Tracing method {} with parameter {}"
        logEvents[0].argumentArray shouldBe arrayOf("testMethod", "testParam")
    }

    test("logTrace with throwable should log trace message with exception") {
        val exception = Exception("Trace exception")
        logTrace("Trace with exception", exception)

        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.TRACE
        logEvents[0].message shouldBe "Trace with exception"
        logEvents[0].throwableProxy?.message shouldBe "Trace exception"
    }

    test("logDebugIf should log when debug is enabled") {
        var lambdaCalled = false
        
        logDebugIf { 
            lambdaCalled = true
            "Debug conditional message"
        }

        lambdaCalled shouldBe true
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.DEBUG
        logEvents[0].message shouldBe "Debug conditional message"
    }

    test("logDebugIf should not call lambda when debug is disabled") {
        testLogger.level = Level.INFO
        var lambdaCalled = false
        
        logDebugIf { 
            lambdaCalled = true
            "This should not be logged"
        }

        lambdaCalled shouldBe false
        val logEvents = listAppender.list
        logEvents.size shouldBe 0
    }

    test("logInfoIf should log when info is enabled") {
        var lambdaCalled = false
        
        logInfoIf { 
            lambdaCalled = true
            "Info conditional message"
        }

        lambdaCalled shouldBe true
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.INFO
        logEvents[0].message shouldBe "Info conditional message"
    }

    test("logInfoIf should not call lambda when info is disabled") {
        testLogger.level = Level.WARN
        var lambdaCalled = false
        
        logInfoIf { 
            lambdaCalled = true
            "This should not be logged"
        }

        lambdaCalled shouldBe false
        val logEvents = listAppender.list
        logEvents.size shouldBe 0
    }

    test("logWarnIf should log when warn is enabled") {
        var lambdaCalled = false
        
        logWarnIf { 
            lambdaCalled = true
            "Warning conditional message"
        }

        lambdaCalled shouldBe true
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.WARN
        logEvents[0].message shouldBe "Warning conditional message"
    }

    test("logWarnIf should not call lambda when warn is disabled") {
        testLogger.level = Level.ERROR
        var lambdaCalled = false
        
        logWarnIf { 
            lambdaCalled = true
            "This should not be logged"
        }

        lambdaCalled shouldBe false
        val logEvents = listAppender.list
        logEvents.size shouldBe 0
    }

    test("logErrorIf should log when error is enabled") {
        var lambdaCalled = false
        
        logErrorIf { 
            lambdaCalled = true
            "Error conditional message"
        }

        lambdaCalled shouldBe true
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.ERROR
        logEvents[0].message shouldBe "Error conditional message"
    }

    test("logErrorIf should not call lambda when error is disabled") {
        testLogger.level = Level.OFF
        var lambdaCalled = false
        
        logErrorIf { 
            lambdaCalled = true
            "This should not be logged"
        }

        lambdaCalled shouldBe false
        val logEvents = listAppender.list
        logEvents.size shouldBe 0
    }

    test("logTraceIf should log when trace is enabled") {
        var lambdaCalled = false
        
        logTraceIf { 
            lambdaCalled = true
            "Trace conditional message"
        }

        lambdaCalled shouldBe true
        val logEvents = listAppender.list
        logEvents.size shouldBe 1
        logEvents[0].level shouldBe Level.TRACE
        logEvents[0].message shouldBe "Trace conditional message"
    }

    test("logTraceIf should not call lambda when trace is disabled") {
        testLogger.level = Level.DEBUG
        var lambdaCalled = false
        
        logTraceIf { 
            lambdaCalled = true
            "This should not be logged"
        }

        lambdaCalled shouldBe false
        val logEvents = listAppender.list
        logEvents.size shouldBe 0
    }

    test("logging functions should work from different objects") {
        val testObject1 = "Test String"
        val testObject2 = listOf(1, 2, 3)
        
        testObject1.logInfo("Message from string")
        testObject2.logInfo("Message from list")

        val logEvents = listAppender.list
        logEvents.size shouldBe 2
        logEvents[0].message shouldBe "Message from string"
        logEvents[1].message shouldBe "Message from list"
    }
})