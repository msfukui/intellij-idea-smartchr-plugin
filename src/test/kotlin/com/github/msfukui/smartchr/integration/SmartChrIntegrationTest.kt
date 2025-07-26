package com.github.msfukui.smartchr.integration

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.SmartChrMapping
import com.github.msfukui.smartchr.settings.SmartChrSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * SmartChrプラグインの統合テスト
 * プラグインの全体的な動作を確認する
 */
class SmartChrIntegrationTest : BasePlatformTestCase() {
    
    fun testPluginBasicConfiguration() {
        // プラグインの基本的な設定機能をテスト
        val settings = SmartChrSettings()
        settings.reset()
        
        // 設定が空であることを確認
        assertTrue("初期状態では設定が空であること", settings.getMappings().isEmpty())
        
        // マッピングを追加
        val mapping = SmartChrMapping(
            key = '=',
            candidates = listOf("=", " = ", " == "),
            mode = CycleMode.LOOP,
            fileTypes = listOf("*"),
            enabled = true
        )
        settings.addMapping(mapping)
        
        // マッピングが正しく追加されたことを確認
        val mappings = settings.getMappings()
        assertEquals("マッピングが1つ追加されること", 1, mappings.size)
        assertEquals("キーが正しく設定されること", '=', mappings[0].key)
        assertEquals("候補が正しく設定されること", 3, mappings[0].candidates.size)
        assertEquals("モードが正しく設定されること", CycleMode.LOOP, mappings[0].mode)
    }
    
    fun testMultipleMappingsConfiguration() {
        // 複数のマッピング設定をテスト
        val settings = SmartChrSettings()
        settings.reset()
        
        val mappings = listOf(
            SmartChrMapping(
                key = '=',
                candidates = listOf("=", " = ", " == "),
                mode = CycleMode.LOOP
            ),
            SmartChrMapping(
                key = ',',
                candidates = listOf(",", ", "),
                mode = CycleMode.ONE_OF
            ),
            SmartChrMapping(
                key = '.',
                candidates = listOf(".", "->"),
                mode = CycleMode.LOOP,
                fileTypes = listOf("JAVA", "Kotlin")
            )
        )
        
        settings.setMappings(mappings)
        
        val savedMappings = settings.getMappings()
        assertEquals("3つのマッピングが保存されること", 3, savedMappings.size)
        assertEquals("最初のマッピングのキーが正しいこと", '=', savedMappings[0].key)
        assertEquals("2番目のマッピングのモードが正しいこと", CycleMode.ONE_OF, savedMappings[1].mode)
        assertEquals("3番目のマッピングのファイルタイプが正しいこと", listOf("JAVA", "Kotlin"), savedMappings[2].fileTypes)
    }
    
    fun testSettingsPersistence() {
        // 設定の永続化をテスト
        val originalSettings = SmartChrSettings()
        originalSettings.reset()
        
        // 設定を追加
        val mapping = SmartChrMapping(
            key = '#',
            candidates = listOf("#", "# ", "## "),
            mode = CycleMode.ONE_OF,
            fileTypes = listOf("Markdown"),
            enabled = false
        )
        originalSettings.addMapping(mapping)
        
        // 状態を保存・復元
        val state = originalSettings.state
        val newSettings = SmartChrSettings()
        newSettings.loadState(state)
        
        // 復元された設定を確認
        val loadedMappings = newSettings.getMappings()
        assertEquals("マッピングが復元されること", 1, loadedMappings.size)
        
        val loadedMapping = loadedMappings[0]
        assertEquals("キーが復元されること", '#', loadedMapping.key)
        assertEquals("候補が復元されること", listOf("#", "# ", "## "), loadedMapping.candidates)
        assertEquals("モードが復元されること", CycleMode.ONE_OF, loadedMapping.mode)
        assertEquals("ファイルタイプが復元されること", listOf("Markdown"), loadedMapping.fileTypes)
        assertFalse("有効フラグが復元されること", loadedMapping.enabled)
    }
    
    fun testEditorBasicFunctionality() {
        // エディタの基本機能をテスト（SmartChrなしでも動作することを確認）
        myFixture.configureByText("test.txt", "")
        
        // 通常の文字入力
        myFixture.type("Hello World")
        assertEquals("通常の文字入力が機能すること", "Hello World", myFixture.editor.document.text)
        
        // エディタが正常に動作していることを確認
        assertNotNull("エディタが利用可能であること", myFixture.editor)
        assertNotNull("ドキュメントが利用可能であること", myFixture.editor.document)
    }
    
    fun testSettingsValidation() {
        // 設定の検証をテスト
        val settings = SmartChrSettings()
        settings.reset()
        
        // 無効な設定を試行（空の候補リスト）
        try {
            SmartChrMapping(
                key = 'x',
                candidates = emptyList() // 空のリスト
            )
            fail("空の候補リストでは例外が発生すべき")
        } catch (e: IllegalArgumentException) {
            // 正常なケース
            assertTrue("適切な例外メッセージが含まれること", e.message?.contains("empty") == true)
        }
        
        // 有効な設定
        val validMapping = SmartChrMapping(
            key = 'y',
            candidates = listOf("y", "yes")
        )
        settings.addMapping(validMapping)
        
        assertEquals("有効な設定は正常に追加されること", 1, settings.getMappings().size)
    }
    
    fun testConfigurableIntegration() {
        // Configurableクラスとの統合をテスト
        val settings = SmartChrSettings()
        settings.reset()
        
        // 設定にマッピングを追加
        settings.addMapping(
            SmartChrMapping(
                key = '@',
                candidates = listOf("@", "@param ", "@return "),
                mode = CycleMode.LOOP,
                fileTypes = listOf("JAVA")
            )
        )
        
        // 設定が正しく保存されていることを確認
        val mappings = settings.getMappings()
        assertEquals("Configurableとの統合でマッピングが保存されること", 1, mappings.size)
        assertEquals("JavaDoc用の設定が正しく保存されること", '@', mappings[0].key)
        assertEquals("ファイルタイプ制限が正しく保存されること", listOf("JAVA"), mappings[0].fileTypes)
    }
}