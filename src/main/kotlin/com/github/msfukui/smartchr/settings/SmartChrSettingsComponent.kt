package com.github.msfukui.smartchr.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.platform.PlatformProjectOpenProcessor
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.Desktop
import java.io.File
import java.nio.file.Paths
import javax.swing.JButton
import javax.swing.JPanel

/**
 * SmartChrの設定UIコンポーネント
 * JSON設定ファイルをIntelliJエディタで開く機能を提供（プロジェクト未開時でも対応）
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
        
        panel = FormBuilder.createFormBuilder()
            .addComponent(JBLabel("SmartChr設定"))
            .addSeparator()
            .addComponent(JBLabel("JSON設定ファイルでキーマッピングを設定できます。"))
            .addVerticalGap(10)
            .addLabeledComponent("設定ファイルの場所:", JBLabel(configPath))
            .addVerticalGap(10)
            .addComponent(openConfigButton)
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
     * 設定ファイルをIntelliJエディタで開く（プロジェクト未開時でも対応）
     */
    private fun openConfigFile(configPath: String) {
        val file = File(configPath)
        
        // ファイルが存在しない場合は空のデフォルト設定ファイルを作成
        if (!file.exists()) {
            try {
                jsonConfigService.createDefaultConfigFile(configPath)
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
        }
        
        // IntelliJエディタで開くことを試行
        ApplicationManager.getApplication().invokeLater {
            try {
                openWithIntelliJEditor(file)
            } catch (e: Exception) {
                e.printStackTrace()
                // IntelliJエディタで開けない場合はシステムエディタにフォールバック
                openWithSystemEditor(file)
            }
        }
    }
    
    /**
     * IntelliJエディタでファイルを開く（PlatformProjectOpenProcessorを使用）
     */
    private fun openWithIntelliJEditor(file: File) {
        // ファイルをVirtualFileに変換
        LocalFileSystem.getInstance().refresh(false)
        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(file.absolutePath)
            ?: throw Exception("Could not find virtual file for: ${file.absolutePath}")
        
        // PlatformProjectOpenProcessorでファイルを開く
        val projectOpenProcessor = PlatformProjectOpenProcessor.getInstance()
        val projectManagerEx = ProjectManagerEx.getInstanceEx()
        
        // ファイルが単体で開けるかチェック
        if (projectOpenProcessor.canOpenProject(virtualFile)) {
            projectOpenProcessor.doOpenProject(virtualFile, null, false)
        } else {
            // 単体で開けない場合は、ファイルのディレクトリを一時プロジェクトとして開く
            val projectPath = Paths.get(file.parent)
            val parentVirtualFile = LocalFileSystem.getInstance().findFileByPath(file.parent)
            if (parentVirtualFile != null) {
                projectOpenProcessor.doOpenProject(parentVirtualFile, null, false)
                // プロジェクトが開かれた後にファイルをエディタで開く
                ApplicationManager.getApplication().invokeLater {
                    val project = projectManagerEx.openProjects.firstOrNull()
                    if (project != null) {
                        val fileEditorManager = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(project)
                        fileEditorManager.openFile(virtualFile, true)
                    }
                }
            } else {
                throw Exception("Could not find parent directory: ${file.parent}")
            }
        }
    }
    
    /**
     * システムの既定エディタでファイルを開く（フォールバック用）
     */
    private fun openWithSystemEditor(file: File) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}