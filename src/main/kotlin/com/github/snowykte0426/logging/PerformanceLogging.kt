package com.github.snowykte0426.logging

import kotlin.system.measureTimeMillis

/**
 * Utility functions for performance logging and method execution tracking.
 */

/**
 * Execute a block of code and log the execution time.
 * 
 * @param operation Description of the operation being timed
 * @param block The code block to execute and measure
 * @return The result of the block execution
 */
inline fun <T> Any.logExecutionTime(operation: String, block: () -> T): T {
    val logger = logger()
    var result: T
    val timeMs = measureTimeMillis {
        result = block()
    }
    logger.debug("$operation completed in ${timeMs}ms")
    return result
}

/**
 * Execute a block of code and log the execution time with custom logger.
 * 
 * @param logger Custom logger to use
 * @param operation Description of the operation being timed
 * @param block The code block to execute and measure
 * @return The result of the block execution
 */
inline fun <T> logExecutionTime(logger: org.slf4j.Logger, operation: String, block: () -> T): T {
    var result: T
    val timeMs = measureTimeMillis {
        result = block()
    }
    logger.debug("$operation completed in ${timeMs}ms")
    return result
}

/**
 * Execute a block with method entry and exit logging.
 * 
 * @param methodName Name of the method being tracked
 * @param params Optional parameters to log
 * @param block The code block to execute
 * @return The result of the block execution
 */
inline fun <T> Any.logMethodExecution(
    methodName: String, 
    params: Map<String, Any?> = emptyMap(),
    block: () -> T
): T {
    val logger = logger()
    val paramsString = if (params.isNotEmpty()) {
        params.entries.joinToString(", ") { "${it.key}=${it.value}" }
    } else ""
    
    logger.debug("Entering method: $methodName${if (paramsString.isNotEmpty()) " with params: $paramsString" else ""}")
    
    return try {
        val result = block()
        logger.debug("Exiting method: $methodName successfully")
        result
    } catch (e: Exception) {
        logger.error("Exiting method: $methodName with error: ${e.message}", e)
        throw e
    }
}

/**
 * Execute a block with detailed performance logging including memory usage.
 * 
 * @param operation Description of the operation
 * @param block The code block to execute
 * @return The result of the block execution
 */
inline fun <T> Any.logPerformance(operation: String, block: () -> T): T {
    val logger = logger()
    val runtime = Runtime.getRuntime()
    
    // Force garbage collection to get more accurate memory readings
    System.gc()
    val memoryBefore = runtime.totalMemory() - runtime.freeMemory()
    
    var result: T
    val timeMs = measureTimeMillis {
        result = block()
    }
    
    System.gc()
    val memoryAfter = runtime.totalMemory() - runtime.freeMemory()
    val memoryUsed = memoryAfter - memoryBefore
    
    logger.debug("$operation - Time: ${timeMs}ms, Memory used: ${memoryUsed / 1024}KB")
    return result
}

/**
 * Execute a block and log any exceptions that occur.
 * 
 * @param operation Description of the operation
 * @param block The code block to execute
 * @return The result of the block execution, or null if an exception occurred
 */
inline fun <T> Any.logOnException(operation: String, block: () -> T): T? {
    val logger = logger()
    return try {
        block()
    } catch (e: Exception) {
        logger.error("Exception in operation '$operation': ${e.message}", e)
        null
    }
}

/**
 * Execute a block and log warnings for any exceptions, without stopping execution.
 * 
 * @param operation Description of the operation
 * @param defaultValue Value to return if an exception occurs
 * @param block The code block to execute
 * @return The result of the block execution, or defaultValue if an exception occurred
 */
inline fun <T> Any.logWarningOnException(operation: String, defaultValue: T, block: () -> T): T {
    val logger = logger()
    return try {
        block()
    } catch (e: Exception) {
        logger.warn("Warning in operation '$operation': ${e.message}, returning default value", e)
        defaultValue
    }
}
