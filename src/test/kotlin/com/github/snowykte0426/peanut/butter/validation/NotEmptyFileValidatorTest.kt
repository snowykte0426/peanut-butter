package com.github.snowykte0426.peanut.butter.validation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NotEmptyFileValidatorTest : FunSpec({

    lateinit var validator: NotEmptyFileValidator
    lateinit var context: ConstraintValidatorContext

    beforeEach {
        validator = NotEmptyFileValidator()
        context = mock<ConstraintValidatorContext>()
    }

    test("should return false for null file") {
        val result = validator.isValid(null, context)
        result shouldBe false
    }

    test("should return false for empty file") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(true)

        val result = validator.isValid(mockFile, context)
        result shouldBe false
    }

    test("should return true for non-empty file") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(false)

        val result = validator.isValid(mockFile, context)
        result shouldBe true
    }

    test("should handle file with zero size as empty") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(true)
        whenever(mockFile.size).thenReturn(0L)

        val result = validator.isValid(mockFile, context)
        result shouldBe false
    }

    test("should handle file with positive size as non-empty") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(false)
        whenever(mockFile.size).thenReturn(1024L)

        val result = validator.isValid(mockFile, context)
        result shouldBe true
    }

    test("should validate file with original filename") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(false)
        whenever(mockFile.originalFilename).thenReturn("test.txt")

        val result = validator.isValid(mockFile, context)
        result shouldBe true
    }

    test("should validate file without original filename but with content") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(false)
        whenever(mockFile.originalFilename).thenReturn(null)

        val result = validator.isValid(mockFile, context)
        result shouldBe true
    }

    test("should handle file with content type") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(false)
        whenever(mockFile.contentType).thenReturn("text/plain")

        val result = validator.isValid(mockFile, context)
        result shouldBe true
    }

    test("should handle file with bytes") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(false)
        whenever(mockFile.bytes).thenReturn("test content".toByteArray())

        val result = validator.isValid(mockFile, context)
        result shouldBe true
    }

    test("should validate multiple scenarios consistently") {
        val emptyFile1 = mock<MultipartFile>()
        whenever(emptyFile1.isEmpty).thenReturn(true)
        
        val emptyFile2 = mock<MultipartFile>()
        whenever(emptyFile2.isEmpty).thenReturn(true)
        whenever(emptyFile2.size).thenReturn(0L)

        val nonEmptyFile1 = mock<MultipartFile>()
        whenever(nonEmptyFile1.isEmpty).thenReturn(false)
        whenever(nonEmptyFile1.size).thenReturn(100L)
        
        val nonEmptyFile2 = mock<MultipartFile>()
        whenever(nonEmptyFile2.isEmpty).thenReturn(false)
        whenever(nonEmptyFile2.originalFilename).thenReturn("document.pdf")

        validator.isValid(null, context) shouldBe false
        validator.isValid(emptyFile1, context) shouldBe false
        validator.isValid(emptyFile2, context) shouldBe false
        validator.isValid(nonEmptyFile1, context) shouldBe true
        validator.isValid(nonEmptyFile2, context) shouldBe true
    }

    test("should not depend on context for validation logic") {
        val mockFile = mock<MultipartFile>()
        whenever(mockFile.isEmpty).thenReturn(false)

        val context1 = mock<ConstraintValidatorContext>()
        val context2 = mock<ConstraintValidatorContext>()

        validator.isValid(mockFile, context1) shouldBe true
        validator.isValid(mockFile, context2) shouldBe true

        val result1 = validator.isValid(mockFile, context1)
        val result2 = validator.isValid(mockFile, context2)
        result1 shouldBe result2
    }
})