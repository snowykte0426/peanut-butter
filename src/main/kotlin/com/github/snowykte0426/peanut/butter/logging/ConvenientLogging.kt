package com.github.snowykte0426.peanut.butter.logging

/**
 * Convenient logging extension functions that work with any object.
 * These functions automatically get the logger for the calling class.
 */

/**
 * Log a debug message.
 */
fun Any.logDebug(message: String) {
    logger().debug(message)
}

/**
 * Log a debug message with arguments.
 */
fun Any.logDebug(message: String, vararg args: Any?) {
    logger().debug(message, *args)
}

/**
 * Log a debug message with throwable.
 */
fun Any.logDebug(message: String, throwable: Throwable) {
    logger().debug(message, throwable)
}

/**
 * Log an info message.
 */
fun Any.logInfo(message: String) {
    logger().info(message)
}

/**
 * Log an info message with arguments.
 */
fun Any.logInfo(message: String, vararg args: Any?) {
    logger().info(message, *args)
}

/**
 * Log an info message with throwable.
 */
fun Any.logInfo(message: String, throwable: Throwable) {
    logger().info(message, throwable)
}

/**
 * Log a warn message.
 */
fun Any.logWarn(message: String) {
    logger().warn(message)
}

/**
 * Log a warn message with arguments.
 */
fun Any.logWarn(message: String, vararg args: Any?) {
    logger().warn(message, *args)
}

/**
 * Log a warn message with throwable.
 */
fun Any.logWarn(message: String, throwable: Throwable) {
    logger().warn(message, throwable)
}

/**
 * Log an error message.
 */
fun Any.logError(message: String) {
    logger().error(message)
}

/**
 * Log an error message with arguments.
 */
fun Any.logError(message: String, vararg args: Any?) {
    logger().error(message, *args)
}

/**
 * Log an error message with throwable.
 */
fun Any.logError(message: String, throwable: Throwable) {
    logger().error(message, throwable)
}

/**
 * Log a trace message.
 */
fun Any.logTrace(message: String) {
    logger().trace(message)
}

/**
 * Log a trace message with arguments.
 */
fun Any.logTrace(message: String, vararg args: Any?) {
    logger().trace(message, *args)
}

/**
 * Log a trace message with throwable.
 */
fun Any.logTrace(message: String, throwable: Throwable) {
    logger().trace(message, throwable)
}

/**
 * Conditional logging functions - only log if the level is enabled
 * to avoid expensive string operations when logging is disabled.
 */

/**
 * Log debug message only if debug level is enabled.
 */
inline fun Any.logDebugIf(message: () -> String) {
    val logger = logger()
    if (logger.isDebugEnabled) {
        logger.debug(message())
    }
}

/**
 * Log info message only if info level is enabled.
 */
inline fun Any.logInfoIf(message: () -> String) {
    val logger = logger()
    if (logger.isInfoEnabled) {
        logger.info(message())
    }
}

/**
 * Log warn message only if warn level is enabled.
 */
inline fun Any.logWarnIf(message: () -> String) {
    val logger = logger()
    if (logger.isWarnEnabled) {
        logger.warn(message())
    }
}

/**
 * Log error message only if error level is enabled.
 */
inline fun Any.logErrorIf(message: () -> String) {
    val logger = logger()
    if (logger.isErrorEnabled) {
        logger.error(message())
    }
}

/**
 * Log trace message only if trace level is enabled.
 */
inline fun Any.logTraceIf(message: () -> String) {
    val logger = logger()
    if (logger.isTraceEnabled) {
        logger.trace(message())
    }
}
