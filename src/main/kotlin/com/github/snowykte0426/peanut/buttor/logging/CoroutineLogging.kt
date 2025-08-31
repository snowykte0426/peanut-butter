package com.github.snowykte0426.peanut.buttor.logging

import kotlinx.coroutines.*
import org.slf4j.MDC
import java.util.UUID
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

/**
 * Coroutine-aware logging extensions that maintain context and provide async-safe logging.
 */

/**
 * Suspend function to log a debug message in coroutine context.
 */
suspend fun Any.logDebugAsync(message: String) {
    withContext(Dispatchers.IO) {
        logger().debug(message)
    }
}

/**
 * Suspend function to log a debug message with arguments in coroutine context.
 */
suspend fun Any.logDebugAsync(message: String, vararg args: Any?) {
    withContext(Dispatchers.IO) {
        logger().debug(message, *args)
    }
}

/**
 * Suspend function to log an info message in coroutine context.
 */
suspend fun Any.logInfoAsync(message: String) {
    withContext(Dispatchers.IO) {
        logger().info(message)
    }
}

/**
 * Suspend function to log an info message with arguments in coroutine context.
 */
suspend fun Any.logInfoAsync(message: String, vararg args: Any?) {
    withContext(Dispatchers.IO) {
        logger().info(message, *args)
    }
}

/**
 * Suspend function to log a warn message in coroutine context.
 */
suspend fun Any.logWarnAsync(message: String) {
    withContext(Dispatchers.IO) {
        logger().warn(message)
    }
}

/**
 * Suspend function to log a warn message with arguments in coroutine context.
 */
suspend fun Any.logWarnAsync(message: String, vararg args: Any?) {
    withContext(Dispatchers.IO) {
        logger().warn(message, *args)
    }
}

/**
 * Suspend function to log an error message in coroutine context.
 */
suspend fun Any.logErrorAsync(message: String) {
    withContext(Dispatchers.IO) {
        logger().error(message)
    }
}

/**
 * Suspend function to log an error message with arguments in coroutine context.
 */
suspend fun Any.logErrorAsync(message: String, vararg args: Any?) {
    withContext(Dispatchers.IO) {
        logger().error(message, *args)
    }
}

/**
 * Suspend function to log an error message with throwable in coroutine context.
 */
suspend fun Any.logErrorAsync(message: String, throwable: Throwable) {
    withContext(Dispatchers.IO) {
        logger().error(message, throwable)
    }
}

/**
 * Execute a suspend block and log the execution time in coroutine context.
 *
 * @param operation Description of the operation being timed
 * @param block The suspend block to execute and measure
 * @return The result of the block execution
 */
suspend inline fun <T> Any.logExecutionTimeAsync(operation: String, crossinline block: suspend () -> T): T {
    val logger = logger()
    var result: T
    val timeMs = measureTimeMillis {
        result = block()
    }
    withContext(Dispatchers.IO) {
        logger.debug("$operation completed in ${timeMs}ms")
    }
    return result
}

/**
 * Execute a suspend block with method entry and exit logging in coroutine context.
 *
 * @param methodName Name of the method being tracked
 * @param params Optional parameters to log
 * @param block The suspend block to execute
 * @return The result of the block execution
 */
suspend inline fun <T> Any.logMethodExecutionAsync(
    methodName: String,
    params: Map<String, Any?> = emptyMap(),
    crossinline block: suspend () -> T
): T {
    val logger = logger()
    val paramsString = if (params.isNotEmpty()) {
        params.entries.joinToString(", ") { "${it.key}=${it.value}" }
    } else ""

    withContext(Dispatchers.IO) {
        logger.debug("Entering method: $methodName${if (paramsString.isNotEmpty()) " with params: $paramsString" else ""}")
    }

    return try {
        val result = block()
        withContext(Dispatchers.IO) {
            logger.debug("Exiting method: $methodName successfully")
        }
        result
    } catch (e: Exception) {
        withContext(Dispatchers.IO) {
            logger.error("Exiting method: $methodName with error: ${e.message}", e)
        }
        throw e
    }
}

/**
 * Execute a suspend block and log any exceptions that occur in coroutine context.
 *
 * @param operation Description of the operation
 * @param block The suspend block to execute
 * @return The result of the block execution, or null if an exception occurred
 */
suspend inline fun <T> Any.logOnExceptionAsync(operation: String, crossinline block: suspend () -> T): T? {
    val logger = logger()
    return try {
        block()
    } catch (e: Exception) {
        withContext(Dispatchers.IO) {
            logger.error("Exception in operation '$operation': ${e.message}", e)
        }
        null
    }
}

/**
 * Retry a suspend operation with exponential backoff and logging.
 *
 * @param operation Description of the operation
 * @param maxAttempts Maximum number of retry attempts
 * @param initialDelay Initial delay between retries
 * @param maxDelay Maximum delay between retries
 * @param backoffFactor Multiplier for delay on each retry
 * @param block The suspend block to retry
 * @return The result of the block execution
 * @throws Exception if all retry attempts fail
 */
suspend inline fun <T> Any.retryWithLogging(
    operation: String,
    maxAttempts: Int = 3,
    initialDelay: Long = 1000L,
    maxDelay: Long = 30000L,
    backoffFactor: Double = 2.0,
    crossinline block: suspend () -> T
): T {
    val logger = logger()
    var attempt = 1
    var delay = initialDelay
    var lastException: Exception? = null

    while (attempt <= maxAttempts) {
        try {
            if (attempt > 1) {
                withContext(Dispatchers.IO) {
                    logger.info("Retrying operation '$operation' - attempt $attempt/$maxAttempts after ${delay}ms delay")
                }
                delay(delay)
            } else {
                withContext(Dispatchers.IO) {
                    logger.debug("Starting operation '$operation' - attempt $attempt/$maxAttempts")
                }
            }

            return block()
        } catch (e: Exception) {
            lastException = e
            withContext(Dispatchers.IO) {
                if (attempt < maxAttempts) {
                    logger.warn("Operation '$operation' failed on attempt $attempt/$maxAttempts: ${e.message}")
                } else {
                    logger.error("Operation '$operation' failed on final attempt $attempt/$maxAttempts: ${e.message}", e)
                }
            }

            attempt++
            delay = minOf((delay * backoffFactor).toLong(), maxDelay)
        }
    }

    throw lastException ?: RuntimeException("All retry attempts failed for operation: $operation")
}

/**
 * Enhanced coroutine context with logging metadata.
 */
class LoggingCoroutineContext(
    private val correlationId: String = generateCorrelationId()
) : AbstractCoroutineContextElement(LoggingCoroutineContext) {

    companion object Key : CoroutineContext.Key<LoggingCoroutineContext> {
        fun generateCorrelationId(): String = UUID.randomUUID().toString().take(8)
    }

    fun getCorrelationId(): String = correlationId
}

/**
 * Execute a block with enhanced logging context that includes correlation ID.
 *
 * @param correlationId Optional correlation ID for tracking related operations
 * @param block The suspend block to execute with logging context
 * @return The result of the block execution
 */
suspend inline fun <T> Any.withLoggingContext(
    correlationId: String? = null,
    crossinline block: suspend CoroutineScope.() -> T
): T {
    val context = LoggingCoroutineContext(correlationId ?: LoggingCoroutineContext.generateCorrelationId())

    return withContext(context) {
        val previousCorrelationId = MDC.get("correlationId")
        try {
            MDC.put("correlationId", context.getCorrelationId())
            block()
        } finally {
            if (previousCorrelationId != null) {
                MDC.put("correlationId", previousCorrelationId)
            } else {
                MDC.remove("correlationId")
            }
        }
    }
}

/**
 * Log the current coroutine context information.
 */
suspend fun Any.logCoroutineContext() {
    val logger = logger()
    withContext(Dispatchers.IO) {
        logger.debug("Coroutine context - Current thread: ${Thread.currentThread().name}")
    }
}

/**
 * Parallel execution with individual operation logging.
 *
 * @param operations List of suspend operations to execute in parallel
 * @return List of results in the same order as operations
 */
suspend inline fun <T> Any.executeParallelWithLogging(
    crossinline operations: suspend () -> List<suspend () -> T>
): List<T> {
    val logger = logger()
    val operationList = operations()

    withContext(Dispatchers.IO) {
        logger.info("Starting parallel execution of ${operationList.size} operations")
    }

    val startTime = System.currentTimeMillis()

    return try {
        coroutineScope {
            val results = operationList.mapIndexed { index, operation ->
                async {
                    val opStartTime = System.currentTimeMillis()
                    try {
                        val result = operation()
                        val opDuration = System.currentTimeMillis() - opStartTime
                        withContext(Dispatchers.IO) {
                            logger.debug("Parallel operation $index completed in ${opDuration}ms")
                        }
                        result
                    } catch (e: Exception) {
                        val opDuration = System.currentTimeMillis() - opStartTime
                        withContext(Dispatchers.IO) {
                            logger.error("Parallel operation $index failed after ${opDuration}ms: ${e.message}", e)
                        }
                        throw e
                    }
                }
            }.awaitAll()

            val totalDuration = System.currentTimeMillis() - startTime
            withContext(Dispatchers.IO) {
                logger.info("Parallel execution completed in ${totalDuration}ms")
            }

            results
        }
    } catch (e: Exception) {
        val totalDuration = System.currentTimeMillis() - startTime
        withContext(Dispatchers.IO) {
            logger.error("Parallel execution failed after ${totalDuration}ms: ${e.message}", e)
        }
        throw e
    }
}
