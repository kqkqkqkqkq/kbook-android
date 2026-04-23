package ru.k.kbook.domain

import io.qameta.allure.Description
import io.qameta.allure.Owner
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import io.qameta.allure.junit4.DisplayName
import junit.framework.TestCase.assertEquals
import org.junit.Test

class KbjuCalculatorTest {

    @Test
    @DisplayName("Name")
    @Description("Description")
    @Severity(SeverityLevel.MINOR)
    @Owner("k")
    fun `GIVEN WHEN THEN`() {
        assertEquals(1, 1)
    }
}