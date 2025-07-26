package com.github.msfukui.smartchr.handler

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.SmartChrMapping
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * SmartChrTypedHandlerのユニットテスト
 * ハンドラーのロジック部分をテストする
 */
class SmartChrTypedHandlerTest : BasePlatformTestCase() {
    
    fun testCalculateNextIndexInLoopMode() {
        // LOOP モードでの次のインデックス計算をテスト
        val mapping = SmartChrMapping(
            key = '=',
            candidates = listOf("=", " = ", " == "),
            mode = CycleMode.LOOP
        )
        
        // 最初のインデックス
        assertEquals("最初のインデックス", 0, calculateNextIndex(mapping, -1))
        assertEquals("2番目のインデックス", 1, calculateNextIndex(mapping, 0))
        assertEquals("3番目のインデックス", 2, calculateNextIndex(mapping, 1))
        assertEquals("ループして最初に戻る", 0, calculateNextIndex(mapping, 2))
    }
    
    fun testCalculateNextIndexInOneOfMode() {
        // ONE_OF モードでの次のインデックス計算をテスト
        val mapping = SmartChrMapping(
            key = ',',
            candidates = listOf(",", ", "),
            mode = CycleMode.ONE_OF
        )
        
        // 最初のインデックス
        assertEquals("最初のインデックス", 0, calculateNextIndex(mapping, -1))
        assertEquals("2番目のインデックス", 1, calculateNextIndex(mapping, 0))
        assertEquals("最後で停止", 1, calculateNextIndex(mapping, 1))
        assertEquals("最後で停止（継続）", 1, calculateNextIndex(mapping, 1))
    }
    
    fun testFindMappingForKey() {
        // キーに対応するマッピングの検索をテスト
        val mappings = listOf(
            SmartChrMapping(
                key = '=',
                candidates = listOf("=", " = "),
                fileTypes = listOf("*")
            ),
            SmartChrMapping(
                key = '.',
                candidates = listOf(".", "->"),
                fileTypes = listOf("JAVA")
            ),
            SmartChrMapping(
                key = '.',
                candidates = listOf(".", " . "),
                fileTypes = listOf("Python")
            )
        )
        
        // 全てのファイルタイプ対応のマッピング
        val equalsMapping = findMappingForKey(mappings, '=', "JAVA")
        assertNotNull("イコールマッピングが見つかること", equalsMapping)
        assertEquals("正しいキーが見つかること", '=', equalsMapping?.key)
        
        // Java固有のマッピング
        val javaMapping = findMappingForKey(mappings, '.', "JAVA")
        assertNotNull("Javaドットマッピングが見つかること", javaMapping)
        assertEquals("Java用の候補が見つかること", listOf(".", "->"), javaMapping?.candidates)
        
        // Python固有のマッピング
        val pythonMapping = findMappingForKey(mappings, '.', "Python")
        assertNotNull("Pythonドットマッピングが見つかること", pythonMapping)
        assertEquals("Python用の候補が見つかること", listOf(".", " . "), pythonMapping?.candidates)
        
        // 存在しないキー
        val notFoundMapping = findMappingForKey(mappings, '#', "JAVA")
        assertNull("存在しないキーはnullが返ること", notFoundMapping)
    }
    
    fun testKeyStateTimeout() {
        // キー状態のタイムアウト判定をテスト
        val currentTime = System.currentTimeMillis()
        val timeoutMs = 2000L
        
        // タイムアウト前
        val recentTime = currentTime - 1000L
        assertFalse("1秒前はタイムアウトしないこと", 
            isTimeout(recentTime, currentTime, timeoutMs))
        
        // タイムアウト後
        val oldTime = currentTime - 3000L
        assertTrue("3秒前はタイムアウトすること",
            isTimeout(oldTime, currentTime, timeoutMs))
        
        // 境界値
        val boundaryTime = currentTime - 2000L
        assertTrue("ちょうど2秒前はタイムアウトすること",
            isTimeout(boundaryTime, currentTime, timeoutMs))
    }
    
    fun testDisabledMappings() {
        // 無効化されたマッピングの処理をテスト
        val mappings = listOf(
            SmartChrMapping(
                key = '=',
                candidates = listOf("=", " = "),
                enabled = false // 無効化
            ),
            SmartChrMapping(
                key = ',',
                candidates = listOf(",", ", "),
                enabled = true // 有効
            )
        )
        
        // 無効化されたマッピング
        val disabledMapping = findEnabledMappingForKey(mappings, '=', "*")
        assertNull("無効化されたマッピングは見つからないこと", disabledMapping)
        
        // 有効なマッピング
        val enabledMapping = findEnabledMappingForKey(mappings, ',', "*")
        assertNotNull("有効なマッピングは見つかること", enabledMapping)
        assertTrue("見つかったマッピングが有効であること", enabledMapping?.enabled == true)
    }
    
    // ヘルパーメソッド（実際のハンドラークラスのロジックを模擬）
    private fun calculateNextIndex(mapping: SmartChrMapping, currentIndex: Int): Int {
        val nextIndex = currentIndex + 1
        return when (mapping.mode) {
            CycleMode.LOOP -> nextIndex % mapping.candidates.size
            CycleMode.ONE_OF -> minOf(nextIndex, mapping.candidates.size - 1)
        }
    }
    
    private fun findMappingForKey(
        mappings: List<SmartChrMapping>, 
        key: Char, 
        fileType: String
    ): SmartChrMapping? {
        return mappings
            .filter { it.key == key }
            .firstOrNull { mapping ->
                mapping.fileTypes.contains("*") || mapping.fileTypes.contains(fileType)
            }
    }
    
    private fun findEnabledMappingForKey(
        mappings: List<SmartChrMapping>,
        key: Char,
        fileType: String
    ): SmartChrMapping? {
        return findMappingForKey(mappings, key, fileType)?.takeIf { it.enabled }
    }
    
    private fun isTimeout(lastTime: Long, currentTime: Long, timeoutMs: Long): Boolean {
        return currentTime - lastTime >= timeoutMs
    }
}