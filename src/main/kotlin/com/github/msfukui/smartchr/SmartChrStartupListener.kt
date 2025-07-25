package com.github.msfukui.smartchr

import com.github.msfukui.smartchr.handler.SmartChrTypedHandler
import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.editor.actionSystem.EditorActionManager

/**
 * SmartChrプラグインの起動リスナー
 * アプリケーションの起動時にプラグインを初期化する
 */
class SmartChrStartupListener : AppLifecycleListener {
    
    companion object {
        private var smartChrHandler: SmartChrTypedHandler? = null
    }
    
    override fun appFrameCreated(commandLineArgs: List<String>) {
        // TypedActionを取得
        val editorActionManager = EditorActionManager.getInstance()
        val typedAction = editorActionManager.typedAction
        
        // SmartChrハンドラーを作成して設定
        smartChrHandler = SmartChrTypedHandler(typedAction.rawHandler)
        typedAction.setupRawHandler(smartChrHandler!!)
    }
}