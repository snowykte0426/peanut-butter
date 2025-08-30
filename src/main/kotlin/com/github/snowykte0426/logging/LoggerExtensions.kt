package com.github.snowykte0426.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Extension function to get a logger for any class or object.
 * 
 * Usage:
 * ```kotlin
 * class MyClass {
 *     private val logger = logger()
 *     
 *     fun doSomething() {
 *         logger.info("Doing something...")
 *     }
 * }
 * ```
 */
fun Any.logger(): Logger = LoggerFactory.getLogger(this.javaClass)

/**
 * Extension function to get a logger with a specific name.
 * 
 * @param name Custom logger name
 * @return Logger instance with the specified name
 */
fun Any.logger(name: String): Logger = LoggerFactory.getLogger(name)

/**
 * Extension function to get a logger for a specific class.
 * 
 * @param clazz Class to create logger for
 * @return Logger instance for the specified class
 */
fun Any.logger(clazz: Class<*>): Logger = LoggerFactory.getLogger(clazz)

/**
 * Inline extension function for lazy logger initialization.
 * This is useful for performance when you want to avoid logger creation overhead
 * until it's actually needed.
 * 
 * Usage:
 * ```kotlin
 * class MyClass {
 *     private val logger by lazyLogger()
 * }
 * ```
 */
inline fun <reified T : Any> T.lazyLogger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(T::class.java) }

/**
 * Extension function for companion objects to get a logger for the outer class.
 * 
 * Usage:
 * ```kotlin
 * class MyClass {
 *     companion object {
 *         private val logger = companionLogger()
 *     }
 * }
 * ```
 */
fun Any.companionLogger(): Logger {
    val companionClass = this.javaClass
    val enclosingClass = companionClass.enclosingClass
    return if (enclosingClass != null) {
        LoggerFactory.getLogger(enclosingClass)
    } else {
        LoggerFactory.getLogger(companionClass)
    }
}
