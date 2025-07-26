package com.github.msfukui.smartchr.settings

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import javax.swing.JButton
import javax.swing.JLabel

/**
 * SmartChrSettingsComponentのテスト（TDD）
 */
class SmartChrSettingsComponentTest {
    
    private lateinit var component: SmartChrSettingsComponent
    private lateinit var settings: SmartChrSettings
    
    @Before
    fun setUp() {
        settings = SmartChrSettings()
        settings.reset() // 各テスト前に設定をクリア
        component = SmartChrSettingsComponent(settings)
    }
    
    @After
    fun tearDown() {
        // テスト後のクリーンアップ
        val jsonConfigPath = settings.getJsonConfigFilePath()
        val jsonFile = File(jsonConfigPath)
        if (jsonFile.exists()) {
            jsonFile.delete()
        }
    }
    
    @Test
    fun testPanelShouldExist() {
        // Given & When
        val panel = component.panel
        
        // Then
        assertNotNull("パネルが存在すること", panel)
    }
    
    @Test
    fun testShouldDisplayJsonConfigFilePath() {
        // Given
        val panel = component.panel
        
        // When - パネル内のコンポーネントを探す
        val labels = findLabelsInPanel(panel)
        
        // Then
        assertTrue("JSON設定ファイルのパス情報が表示されること", 
                   labels.any { it.text.contains("JSON設定ファイル") })
    }
    
    @Test
    fun testShouldHaveOpenConfigFileButton() {
        // Given
        val panel = component.panel
        
        // When - パネル内のボタンを探す
        val buttons = findButtonsInPanel(panel)
        
        // Then
        assertTrue("設定ファイルを開くボタンが存在すること", 
                   buttons.any { it.text.contains("設定ファイルを開く") })
    }
    
    @Test
    fun testShouldNotHaveCreateConfigFileButton() {
        // Given
        val panel = component.panel
        
        // When - パネル内のボタンを探す
        val buttons = findButtonsInPanel(panel)
        
        // Then
        assertFalse("設定ファイルを作成するボタンが存在しないこと", 
                    buttons.any { it.text.contains("設定ファイルを作成") })
    }
    
    @Test
    fun testIsModifiedShouldReturnFalse() {
        // Given & When
        val isModified = component.isModified(settings)
        
        // Then
        assertFalse("変更状態は常にfalseを返すこと", isModified)
    }
    
    /**
     * パネル内のJLabelコンポーネントを再帰的に探す
     */
    private fun findLabelsInPanel(panel: java.awt.Container): List<JLabel> {
        val labels = mutableListOf<JLabel>()
        
        for (component in panel.components) {
            when (component) {
                is JLabel -> labels.add(component)
                is java.awt.Container -> labels.addAll(findLabelsInPanel(component))
            }
        }
        
        return labels
    }
    
    /**
     * パネル内のJButtonコンポーネントを再帰的に探す
     */
    private fun findButtonsInPanel(panel: java.awt.Container): List<JButton> {
        val buttons = mutableListOf<JButton>()
        
        for (component in panel.components) {
            when (component) {
                is JButton -> buttons.add(component)
                is java.awt.Container -> buttons.addAll(findButtonsInPanel(component))
            }
        }
        
        return buttons
    }
}