package com.github.msfukui.smartchr.settings

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.SmartChrMapping
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

/**
 * JSON設定ファイル機能のテスト（TDD）
 */
class JsonConfigServiceTest {
    
    private lateinit var tempConfigFile: File
    private lateinit var jsonConfigService: JsonConfigService
    
    @Before
    fun setUp() {
        tempConfigFile = Files.createTempFile("smartchr-test", ".json").toFile()
        tempConfigFile.deleteOnExit()
        jsonConfigService = JsonConfigService()
    }
    
    @After
    fun tearDown() {
        tempConfigFile.delete()
    }
    
    @Test
    fun testLoadMappingsFromJsonFile() {
        // Given - JSON設定ファイルを作成
        val jsonContent = """
        {
            "mappings": [
                {
                    "key": "=",
                    "candidates": ["=", " = ", " == "],
                    "mode": "LOOP",
                    "fileTypes": ["*"],
                    "enabled": true
                },
                {
                    "key": ",",
                    "candidates": [",", ", "],
                    "mode": "ONE_OF",
                    "fileTypes": ["JAVA", "Kotlin"],
                    "enabled": false
                }
            ]
        }
        """.trimIndent()
        
        tempConfigFile.writeText(jsonContent)
        
        // When
        val mappings = jsonConfigService.loadMappingsFromFile(tempConfigFile.absolutePath)
        
        // Then
        assertEquals("2つのマッピングが読み込まれること", 2, mappings.size)
        
        val firstMapping = mappings[0]
        assertEquals("最初のキーが正しいこと", '=', firstMapping.key)
        assertEquals("最初の候補数が正しいこと", 3, firstMapping.candidates.size)
        assertEquals("最初の候補内容が正しいこと", listOf("=", " = ", " == "), firstMapping.candidates)
        assertEquals("最初のモードが正しいこと", CycleMode.LOOP, firstMapping.mode)
        assertEquals("最初のファイルタイプが正しいこと", listOf("*"), firstMapping.fileTypes)
        assertTrue("最初のマッピングが有効であること", firstMapping.enabled)
        
        val secondMapping = mappings[1]
        assertEquals("2番目のキーが正しいこと", ',', secondMapping.key)
        assertEquals("2番目のモードが正しいこと", CycleMode.ONE_OF, secondMapping.mode)
        assertEquals("2番目のファイルタイプが正しいこと", listOf("JAVA", "Kotlin"), secondMapping.fileTypes)
        assertFalse("2番目のマッピングが無効であること", secondMapping.enabled)
    }
    
    @Test
    fun testSaveMappingsToJsonFile() {
        // Given
        val mappings = listOf(
            SmartChrMapping(
                key = '#',
                candidates = listOf("#", "## ", "### "),
                mode = CycleMode.ONE_OF,
                fileTypes = listOf("Markdown"),
                enabled = true
            ),
            SmartChrMapping(
                key = '.',
                candidates = listOf(".", "->"),
                mode = CycleMode.LOOP,
                fileTypes = listOf("JAVA", "Kotlin"),
                enabled = false
            )
        )
        
        // When
        jsonConfigService.saveMappingsToFile(mappings, tempConfigFile.absolutePath)
        
        // Then
        assertTrue("ファイルが作成されること", tempConfigFile.exists())
        assertTrue("ファイルサイズが0より大きいこと", tempConfigFile.length() > 0)
        
        val content = tempConfigFile.readText()
        assertTrue("mappingsキーが含まれること", content.contains("\"mappings\""))
        assertTrue("シャープキーが含まれること", content.contains("\"#\""))
        assertTrue("ドットキーが含まれること", content.contains("\".\""))
        assertTrue("Markdownファイルタイプが含まれること", content.contains("\"Markdown\""))
        assertTrue("ONE_OFモードが含まれること", content.contains("\"ONE_OF\""))
        assertTrue("LOOPモードが含まれること", content.contains("\"LOOP\""))
    }
    
    @Test
    fun testRoundTripConsistency() {
        // Given - 元のマッピング
        val originalMappings = listOf(
            SmartChrMapping(
                key = '@',
                candidates = listOf("@", "@param ", "@return "),
                mode = CycleMode.LOOP,
                fileTypes = listOf("JAVA", "Kotlin", "Scala"),
                enabled = true
            )
        )
        
        // When - 保存 → 読み込み
        jsonConfigService.saveMappingsToFile(originalMappings, tempConfigFile.absolutePath)
        val loadedMappings = jsonConfigService.loadMappingsFromFile(tempConfigFile.absolutePath)
        
        // Then - データが一致すること
        assertEquals("マッピング数が一致すること", originalMappings.size, loadedMappings.size)
        
        val original = originalMappings[0]
        val loaded = loadedMappings[0]
        
        assertEquals("キーが一致すること", original.key, loaded.key)
        assertEquals("候補が一致すること", original.candidates, loaded.candidates)
        assertEquals("モードが一致すること", original.mode, loaded.mode)
        assertEquals("ファイルタイプが一致すること", original.fileTypes, loaded.fileTypes)
        assertEquals("有効フラグが一致すること", original.enabled, loaded.enabled)
    }
    
    @Test
    fun testLoadFromNonExistentFile() {
        // Given - 存在しないファイル
        val nonExistentFile = "non-existent-file.json"
        
        // When & Then
        try {
            jsonConfigService.loadMappingsFromFile(nonExistentFile)
            fail("存在しないファイルでは例外が発生すべき")
        } catch (e: Exception) {
            assertTrue("適切な例外メッセージが含まれること", e.message?.contains("not found") == true || e.message?.contains("存在しない") == true)
        }
    }
    
    @Test
    fun testLoadFromInvalidJson() {
        // Given - 無効なJSONファイル
        tempConfigFile.writeText("{ invalid json content }")
        
        // When & Then
        try {
            jsonConfigService.loadMappingsFromFile(tempConfigFile.absolutePath)
            fail("無効なJSONでは例外が発生すべき")
        } catch (e: Exception) {
            assertTrue("適切な例外メッセージが含まれること", e.message != null)
        }
    }
    
    @Test
    fun testSaveEmptyMappings() {
        // Given - 空のマッピングリスト
        val emptyMappings = emptyList<SmartChrMapping>()
        
        // When
        jsonConfigService.saveMappingsToFile(emptyMappings, tempConfigFile.absolutePath)
        
        // Then
        assertTrue("ファイルが作成されること", tempConfigFile.exists())
        
        val content = tempConfigFile.readText()
        assertTrue("mappingsキーが含まれること", content.contains("\"mappings\""))
        assertTrue("空の配列が含まれること", content.contains("[]"))
    }
    
    @Test
    fun testGetDefaultConfigFilePath() {
        // When
        val defaultPath = jsonConfigService.getDefaultConfigFilePath()
        
        // Then
        assertNotNull("デフォルトパスが取得できること", defaultPath)
        assertTrue("JSONファイル拡張子を持つこと", defaultPath.endsWith(".json"))
        assertTrue("smartchrが含まれること", defaultPath.contains("smartchr"))
        assertTrue("絶対パスであること", File(defaultPath).isAbsolute)
    }
    
    @Test
    fun testCreateDefaultConfigFile() {
        // Given - 存在しないファイルパス
        val newConfigPath = tempConfigFile.absolutePath + "_new"
        val newConfigFile = File(newConfigPath)
        newConfigFile.deleteOnExit()
        
        // When
        jsonConfigService.createDefaultConfigFile(newConfigPath)
        
        // Then
        assertTrue("設定ファイルが作成されること", newConfigFile.exists())
        assertTrue("ファイルサイズが0より大きいこと", newConfigFile.length() > 0)
        
        val content = newConfigFile.readText()
        assertTrue("JSON形式であること", content.trim().startsWith("{"))
        assertTrue("mappingsキーが含まれること", content.contains("\"mappings\""))
        assertTrue("閉じ括弧で終わること", content.trim().endsWith("}"))
    }
}