package com.github.msfukui.smartchr.settings

import com.intellij.ide.actions.RevealFileAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.Desktop
import java.io.File
import javax.swing.JButton
import javax.swing.JPanel

/**
 * SmartChrの設定UIコンポーネント
 * JSON設定ファイルへのリンクと設定ファイル作成機能を提供
 */
class SmartChrSettingsComponent(private val settings: SmartChrSettings? = null) {
    
    val panel: JPanel
    private val jsonConfigService = JsonConfigService()
    
    init {
        val actualSettings = settings ?: SmartChrSettings.getInstance()
        val configPath = actualSettings.getJsonConfigFilePath()
        
        // 設定ファイルを開くボタン
        val openConfigButton = JButton("設定ファイルを開く").apply {
            addActionListener {
                openConfigFile(configPath)
            }
        }
        
        // 設定ファイルを作成するボタン
        val createConfigButton = JButton("設定ファイルを作成").apply {
            addActionListener {
                createConfigFile(configPath)
            }
        }
        
        panel = FormBuilder.createFormBuilder()
            .addComponent(JBLabel("SmartChr設定"))
            .addSeparator()
            .addComponent(JBLabel("JSON設定ファイルでキーマッピングを設定できます。"))
            .addVerticalGap(10)
            .addLabeledComponent("設定ファイルの場所:", JBLabel(configPath))
            .addVerticalGap(10)
            .addComponent(openConfigButton)
            .addComponent(createConfigButton)
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
    
    /**
     * 設定ファイルをエディタで開く
     */
    private fun openConfigFile(configPath: String) {
        val file = File(configPath)
        
        // ファイルが存在しない場合は作成
        if (!file.exists()) {
            createConfigFile(configPath)
        }
        
        try {
            // IntelliJエディタで開く
            val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(configPath)
            if (virtualFile != null) {
                val project = ProjectManager.getInstance().openProjects.firstOrNull()
                if (project != null && !project.isDisposed) {
                    FileEditorManager.getInstance(project).openFile(virtualFile, true)
                } else {
                    // プロジェクトが開いていない場合は外部エディタで開く
                    openWithSystemEditor(file)
                }
            } else {
                openWithSystemEditor(file)
            }
        } catch (e: Exception) {
            // 失敗した場合は外部エディタで開く
            openWithSystemEditor(file)
        }
    }
    
    /**
     * システムの既定のエディタで開く
     */
    private fun openWithSystemEditor(file: File) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file)
            }
        } catch (e: Exception) {
            // ログに記録するが、ユーザーには見せない
            e.printStackTrace()
        }
    }
    
    /**
     * 設定ファイルを作成する
     */
    private fun createConfigFile(configPath: String) {
        try {
            jsonConfigService.createDefaultConfigFile(configPath)
        } catch (e: Exception) {
            // エラーが発生した場合もログに記録するのみ
            e.printStackTrace()
        }
    }
}