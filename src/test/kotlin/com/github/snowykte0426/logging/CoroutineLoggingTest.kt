package com.github.snowykte0426.logging

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.should
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class CoroutineLoggingTest : FunSpec({

    test("logInfoAsync should work in coroutine context") {
        runTest {
            val testObject = TestClass()

            testObject.logInfoAsync("Test async logging")
            testObject.logInfoAsync("Test async logging with args: {}", "argument")
        }
    }

    test("logExecutionTimeAsync should measure time correctly") {
        runTest {
            val testObject = TestClass()
            val delay = 100L

            val result = testObject.logExecutionTimeAsync("Test operation") {
                delay(delay)
                "test result"
            }

            result shouldBe "test result"
        }
    }

    test("logMethodExecutionAsync should handle success and failure") {
        runTest {
            val testObject = TestClass()

            val result = testObject.logMethodExecutionAsync(
                "testMethod",
                mapOf("param1" to "value1", "param2" to 42)
            ) {
                delay(50)
                "success"
            }

            result shouldBe "success"

            try {
                testObject.logMethodExecutionAsync("failingMethod") {
                    throw RuntimeException("Test exception")
                }
            } catch (e: RuntimeException) {
                e.message shouldBe "Test exception"
            }
        }
    }

    test("logOnExceptionAsync should handle exceptions gracefully") {
        runTest {
            val testObject = TestClass()

            val result1 = testObject.logOnExceptionAsync("success operation") {
                "success"
            }
            result1 shouldBe "success"

            val result2 = testObject.logOnExceptionAsync("failing operation") {
                throw RuntimeException("Test exception")
            }
            result2 shouldBe null
        }
    }

    test("retryWithLogging should retry failed operations") {
        runTest {
            val testObject = TestClass()
            val attemptCounter = AtomicInteger(0)

            val result = testObject.retryWithLogging(
                operation = "retry test",
                maxAttempts = 5,
                initialDelay = 10L,
                maxDelay = 100L
            ) {
                val attempt = attemptCounter.incrementAndGet()
                if (attempt < 3) {
                    throw RuntimeException("Attempt $attempt failed")
                }
                "success on attempt $attempt"
            }

            result shouldBe "success on attempt 3"
            attemptCounter.get() shouldBe 3
        }
    }

    test("retryWithLogging should throw exception after max attempts") {
        runTest {
            val testObject = TestClass()

            try {
                testObject.retryWithLogging(
                    operation = "always fail",
                    maxAttempts = 2,
                    initialDelay = 10L
                ) {
                    throw RuntimeException("Always fails")
                }
            } catch (e: RuntimeException) {
                e.message shouldBe "Always fails"
            }
        }
    }

    test("withLoggingContext should maintain correlation ID") {
        runTest {
            val testObject = TestClass()

            testObject.withLoggingContext("test-correlation-123") {
                val context = coroutineContext[LoggingCoroutineContext]
                context shouldNotBe null
                context?.getCorrelationId() shouldBe "test-correlation-123"
            }
        }
    }

    test("executeParallelWithLogging should execute operations in parallel") {
        runTest {
            val testObject = TestClass()
            val startTime = System.currentTimeMillis()

            val results = testObject.executeParallelWithLogging {
                listOf(
                    suspend {
                        delay(100)
                        "result1"
                    },
                    suspend {
                        delay(100)
                        "result2"
                    },
                    suspend {
                        delay(100)
                        "result3"
                    }
                )
            }

            val endTime = System.currentTimeMillis()
            val totalTime = endTime - startTime

            results shouldBe listOf("result1", "result2", "result3")
            totalTime should beLessThan(200L)
        }
    }

    test("logCoroutineContext should log context information") {
        runTest {
            val testObject = TestClass()

            testObject.logCoroutineContext()

            testObject.withLoggingContext("ctx-test") {
                testObject.logCoroutineContext()
            }
        }
    }

    test("async logging methods should work with different log levels") {
        runTest {
            val testObject = TestClass()

            testObject.logDebugAsync("Debug message")
            testObject.logInfoAsync("Info message")
            testObject.logWarnAsync("Warn message")
            testObject.logErrorAsync("Error message")
            testObject.logErrorAsync("Error with exception", RuntimeException("test"))
        }
    }
}) {
    private class TestClass {
        val logger = LoggerFactory.getLogger(TestClass::class.java)
    }
}
