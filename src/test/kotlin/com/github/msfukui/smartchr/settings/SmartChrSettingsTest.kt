package com.github.msfukui.smartchr.settings

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.SmartChrMapping
import junit.framework.TestCase

class SmartChrSettingsTest : TestCase() {
    
    private lateinit var settings: SmartChrSettings
    
    override fun setUp() {
        super.setUp()
        settings = SmartChrSettings()
        // 各テスト前に設定をクリア
        settings.reset()
    }
    
    fun testShouldStartWithEmptyMappings() {
        val mappings = settings.getMappings()
        assertTrue(mappings.isEmpty())
    }
    
    fun testShouldAddMapping() {
        // Given
        val mapping = SmartChrMapping(
            key = '=',
            candidates = listOf("=", " = ", " == "),
            mode = CycleMode.LOOP,
            fileTypes = listOf("JAVA"),
            enabled = true
        )
        
        // When
        settings.addMapping(mapping)
        
        // Then
        val mappings = settings.getMappings()
        assertEquals(1, mappings.size)
        
        val savedMapping = mappings[0]
        assertEquals('=', savedMapping.key)
        assertEquals(listOf("=", " = ", " == "), savedMapping.candidates)
        assertEquals(CycleMode.LOOP, savedMapping.mode)
        assertEquals(listOf("JAVA"), savedMapping.fileTypes)
        assertTrue(savedMapping.enabled)
    }
    
    fun testShouldSetMultipleMappings() {
        // Given
        val mappings = listOf(
            SmartChrMapping(
                key = '=',
                candidates = listOf("=", " = "),
                mode = CycleMode.LOOP
            ),
            SmartChrMapping(
                key = ',',
                candidates = listOf(",", ", "),
                mode = CycleMode.ONE_OF
            )
        )
        
        // When
        settings.setMappings(mappings)
        
        // Then
        val savedMappings = settings.getMappings()
        assertEquals(2, savedMappings.size)
        assertEquals('=', savedMappings[0].key)
        assertEquals(',', savedMappings[1].key)
    }
    
    fun testShouldRemoveMappingByIndex() {
        // Given
        val mappings = listOf(
            SmartChrMapping(key = '=', candidates = listOf("=")),
            SmartChrMapping(key = ',', candidates = listOf(",")),
            SmartChrMapping(key = '.', candidates = listOf("."))
        )
        settings.setMappings(mappings)
        
        // When
        settings.removeMapping(1) // Remove comma mapping
        
        // Then
        val remainingMappings = settings.getMappings()
        assertEquals(2, remainingMappings.size)
        assertEquals('=', remainingMappings[0].key)
        assertEquals('.', remainingMappings[1].key)
    }
    
    fun testShouldHandleRemoveWithInvalidIndex() {
        // Given
        settings.addMapping(SmartChrMapping(key = '=', candidates = listOf("=")))
        
        // When
        settings.removeMapping(5) // Invalid index
        
        // Then
        assertEquals(1, settings.getMappings().size) // No change
    }
    
    fun testShouldResetToEmptyMappings() {
        // Given
        settings.addMapping(SmartChrMapping(key = '=', candidates = listOf("=")))
        settings.addMapping(SmartChrMapping(key = ',', candidates = listOf(",")))
        
        // When
        settings.reset()
        
        // Then
        assertTrue(settings.getMappings().isEmpty())
    }
    
    fun testShouldPersistAndLoadState() {
        // Given
        val originalSettings = SmartChrSettings()
        val mapping = SmartChrMapping(
            key = '=',
            candidates = listOf("=", " = "),
            mode = CycleMode.ONE_OF,
            fileTypes = listOf("Kotlin", "JAVA"),
            enabled = false
        )
        originalSettings.addMapping(mapping)
        
        // When - simulate save and load
        val state = originalSettings.state
        val newSettings = SmartChrSettings()
        newSettings.loadState(state)
        
        // Then
        val loadedMappings = newSettings.getMappings()
        assertEquals(1, loadedMappings.size)
        
        val loadedMapping = loadedMappings[0]
        assertEquals('=', loadedMapping.key)
        assertEquals(listOf("=", " = "), loadedMapping.candidates)
        assertEquals(CycleMode.ONE_OF, loadedMapping.mode)
        assertEquals(listOf("Kotlin", "JAVA"), loadedMapping.fileTypes)
        assertFalse(loadedMapping.enabled)
    }
    
    fun testShouldHandleInvalidCycleModeGracefully() {
        // Given - JSON設定ファイルで無効なモードをテスト
        val jsonContent = """
        {
            "mappings": [
                {
                    "key": "=",
                    "candidates": ["="],
                    "mode": "INVALID_MODE",
                    "fileTypes": ["*"],
                    "enabled": true
                }
            ]
        }
        """.trimIndent()
        
        val jsonConfigPath = settings.getJsonConfigFilePath()
        val jsonFile = java.io.File(jsonConfigPath)
        jsonFile.parentFile?.mkdirs()
        jsonFile.writeText(jsonContent)
        
        // When
        val mappings = settings.getMappings()
        
        // Then
        assertEquals(1, mappings.size)
        assertEquals(CycleMode.LOOP, mappings[0].mode) // Defaults to LOOP
    }
}