package com.github.msfukui.smartchr.settings

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

/**
 * SmartChrプラグインの設定画面
 */
class SmartChrConfigurable : Configurable {
    
    private var settingsComponent: SmartChrSettingsComponent? = null
    
    override fun getDisplayName(): String = "SmartChr"
    
    override fun createComponent(): JComponent? {
        settingsComponent = SmartChrSettingsComponent()
        return settingsComponent?.panel
    }
    
    override fun isModified(): Boolean {
        val settings = SmartChrSettings.getInstance()
        return settingsComponent?.isModified(settings) ?: false
    }
    
    override fun apply() {
        val settings = SmartChrSettings.getInstance()
        settingsComponent?.applySettings(settings)
    }
    
    override fun reset() {
        val settings = SmartChrSettings.getInstance()
        settingsComponent?.resetSettings(settings)
    }
    
    override fun disposeUIResources() {
        settingsComponent = null
    }
}