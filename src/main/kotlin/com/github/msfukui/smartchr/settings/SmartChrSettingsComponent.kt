package com.github.msfukui.smartchr.settings

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.SmartChrMapping
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * SmartChrの設定UIコンポーネント
 * 
 * 注：この実装は基本的なプレースホルダーです。
 * 将来的には、マッピングの追加・編集・削除機能を持つ
 * より高度なUIに置き換える予定です。
 */
class SmartChrSettingsComponent {
    
    val panel: JPanel
    
    init {
        panel = FormBuilder.createFormBuilder()
            .addComponent(JBLabel("SmartChr設定"))
            .addSeparator()
            .addComponent(JBLabel("キーマッピングの設定は次のバージョンで実装予定です。"))
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
    
    /**
     * 設定が変更されたかどうかを判定
     */
    fun isModified(settings: SmartChrSettings): Boolean {
        // 現在のバージョンでは常にfalseを返す
        return false
    }
    
    /**
     * 設定を適用
     */
    fun applySettings(settings: SmartChrSettings) {
        // 現在のバージョンでは何もしない
    }
    
    /**
     * 設定をリセット
     */
    fun resetSettings(settings: SmartChrSettings) {
        // 現在のバージョンでは何もしない
    }
}