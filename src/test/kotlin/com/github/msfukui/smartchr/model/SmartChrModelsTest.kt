package com.github.msfukui.smartchr.model

import junit.framework.TestCase

class SmartChrModelsTest : TestCase() {
    
    fun testSmartChrMappingShouldBeCreatedWithValidParameters() {
        val mapping = SmartChrMapping(
            key = '=',
            candidates = listOf("=", " = ", " == "),
            mode = CycleMode.LOOP,
            fileTypes = listOf("JAVA", "Kotlin"),
            enabled = true
        )
        
        assertEquals('=', mapping.key)
        assertEquals(listOf("=", " = ", " == "), mapping.candidates)
        assertEquals(CycleMode.LOOP, mapping.mode)
        assertEquals(listOf("JAVA", "Kotlin"), mapping.fileTypes)
        assertTrue(mapping.enabled)
    }
    
    fun testSmartChrMappingShouldUseDefaultValues() {
        val mapping = SmartChrMapping(
            key = ',',
            candidates = listOf(",", ", ")
        )
        
        assertEquals(CycleMode.LOOP, mapping.mode)
        assertEquals(listOf("*"), mapping.fileTypes)
        assertTrue(mapping.enabled)
    }
    
    fun testSmartChrMappingShouldThrowExceptionForEmptyCandidates() {
        try {
            SmartChrMapping(
                key = '=',
                candidates = emptyList()
            )
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }
    
    fun testKeyStateShouldBeCreatedWithDefaultValues() {
        val keyState = KeyState()
        
        assertEquals(0, keyState.currentIndex)
        assertNull(keyState.lastInserted)
        assertEquals(0L, keyState.lastTypedTime)
    }
    
    fun testKeyStateShouldBeCreatedWithSpecifiedValues() {
        val keyState = KeyState(
            currentIndex = 2,
            lastInserted = " = ",
            lastTypedTime = 1234567890L
        )
        
        assertEquals(2, keyState.currentIndex)
        assertEquals(" = ", keyState.lastInserted)
        assertEquals(1234567890L, keyState.lastTypedTime)
    }
    
    fun testCycleModeEnumShouldHaveCorrectValues() {
        assertEquals(2, CycleMode.values().size)
        assertTrue(CycleMode.values().contains(CycleMode.ONE_OF))
        assertTrue(CycleMode.values().contains(CycleMode.LOOP))
    }
}