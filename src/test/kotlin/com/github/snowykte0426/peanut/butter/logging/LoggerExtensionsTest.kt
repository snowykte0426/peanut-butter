package com.github.snowykte0426.peanut.butter.logging

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.slf4j.Logger

class LoggerExtensionsTest : FunSpec({
    
    test("logger() should return logger with correct name") {
        val testInstance = TestClass()
        val logger = testInstance.logger()
        
        logger.name shouldBe "logging.com.github.snowykte0426.peanut.butter.TestClass"
    }
    
    test("logger(name) should return logger with custom name") {
        val testInstance = TestClass()
        val customName = "my.custom.logger"
        val logger = testInstance.logger(customName)
        
        logger.name shouldBe customName
    }
    
    test("logger(class) should return logger for specific class") {
        val testInstance = TestClass()
        val logger = testInstance.logger(String::class.java)
        
        logger.name shouldBe "java.lang.String"
    }
    
    test("lazyLogger should create logger lazily") {
        val testInstance = TestClass()
        val lazyLogger by testInstance.lazyLogger()
        
        lazyLogger.name shouldBe "logging.com.github.snowykte0426.peanut.butter.TestClass"
    }
    
    test("companionLogger should return logger for enclosing class") {
        val logger = TestClassWithCompanion.logger()
        
        logger.name shouldBe "logging.com.github.snowykte0426.peanut.butter.TestClassWithCompanion"
    }
    
    test("convenient logging functions should work") {
        val testInstance = TestClass()

        testInstance.logDebug("Debug message")
        testInstance.logInfo("Info message")
        testInstance.logWarn("Warn message")
        testInstance.logError("Error message")
        testInstance.logTrace("Trace message")

        testInstance.logInfo("Info with args: {} and {}", "arg1", "arg2")

        val exception = RuntimeException("Test exception")
        testInstance.logError("Error with exception", exception)
    }
    
    test("conditional logging functions should work") {
        val testInstance = TestClass()

        testInstance.logDebugIf { "Debug message" }
        testInstance.logInfoIf { "Info message" }
        testInstance.logWarnIf { "Warn message" }
        testInstance.logErrorIf { "Error message" }
        testInstance.logTraceIf { "Trace message" }
    }
    
    test("logExecutionTime should measure and return correct result") {
        val testInstance = TestClass()
        val expectedResult = "test result"
        
        val result = testInstance.logExecutionTime("test operation") {
            Thread.sleep(10)
            expectedResult
        }
        
        result shouldBe expectedResult
    }
    
    test("logMethodExecution should track method execution") {
        val testInstance = TestClass()
        val params = mapOf("param1" to "value1", "param2" to 42)
        val expectedResult = "method result"
        
        val result = testInstance.logMethodExecution("testMethod", params) {
            expectedResult
        }
        
        result shouldBe expectedResult
    }
    
    test("logOnException should return null when exception occurs") {
        val testInstance = TestClass()
        
        val result = testInstance.logOnException("failing operation") {
            throw RuntimeException("Test exception")
        }
        
        result shouldBe null
    }
    
    test("logOnException should return result when no exception occurs") {
        val testInstance = TestClass()
        val expectedResult = "success"
        
        val result = testInstance.logOnException("successful operation") {
            expectedResult
        }
        
        result shouldBe expectedResult
    }
    
    test("logWarningOnException should return default value when exception occurs") {
        val testInstance = TestClass()
        val defaultValue = "default"
        
        val result = testInstance.logWarningOnException("failing operation", defaultValue) {
            throw RuntimeException("Test exception")
        }
        
        result shouldBe defaultValue
    }
    
    test("logWarningOnException should return result when no exception occurs") {
        val testInstance = TestClass()
        val expectedResult = "success"
        val defaultValue = "default"
        
        val result = testInstance.logWarningOnException("successful operation", defaultValue) {
            expectedResult
        }
        
        result shouldBe expectedResult
    }
})

private class TestClass

private class TestClassWithCompanion {
    companion object {
        fun logger(): Logger = companionLogger()
    }
}
