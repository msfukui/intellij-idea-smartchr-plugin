package com.github.msfukui.smartchr.handler

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.KeyState
import com.github.msfukui.smartchr.model.SmartChrMapping
import com.github.msfukui.smartchr.settings.SmartChrSettings
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import java.util.concurrent.ConcurrentHashMap

/**
 * SmartChrのキー入力ハンドラー
 * TypedActionHandlerを実装し、キーストロークをインターセプトして処理する
 */
class SmartChrTypedHandler(private val originalHandler: TypedActionHandler) : TypedActionHandler {
    
    // キーごとの状態を管理するマップ
    private val keyStates = ConcurrentHashMap<Char, KeyState>()
    
    // タイムアウト時間（ミリ秒）- この時間を超えたら新しいサイクルとして扱う
    private val timeoutMillis = 2000L
    
    override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
        val settings = SmartChrSettings.getInstance()
        val mapping = findMapping(charTyped, editor, settings)
        
        if (mapping == null || !mapping.enabled) {
            // マッピングがない場合は通常の処理
            originalHandler.execute(editor, charTyped, dataContext)
            return
        }
        
        // SmartChr処理を実行
        handleSmartChr(editor, charTyped, mapping, dataContext)
    }
    
    /**
     * 現在のエディタとファイルタイプに適用されるマッピングを探す
     */
    private fun findMapping(char: Char, editor: Editor, settings: SmartChrSettings): SmartChrMapping? {
        val fileType = getFileType(editor)
        
        return settings.getMappings()
            .filter { it.key == char && it.enabled }
            .firstOrNull { mapping ->
                mapping.fileTypes.contains("*") || mapping.fileTypes.contains(fileType)
            }
    }
    
    /**
     * エディタのファイルタイプを取得
     */
    private fun getFileType(editor: Editor): String {
        val project = editor.project ?: return ""
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
        return psiFile?.fileType?.name ?: ""
    }
    
    /**
     * SmartChrの処理を実行
     */
    private fun handleSmartChr(
        editor: Editor,
        charTyped: Char,
        mapping: SmartChrMapping,
        dataContext: DataContext
    ) {
        val currentTime = System.currentTimeMillis()
        val keyState = keyStates[charTyped] ?: KeyState()
        
        // タイムアウトチェック
        val isTimeout = currentTime - keyState.lastTypedTime > timeoutMillis
        
        // 次の候補インデックスを計算
        val nextIndex = if (isTimeout || keyState.lastInserted == null) {
            0
        } else {
            calculateNextIndex(keyState.currentIndex, mapping)
        }
        
        // 挿入する文字列を取得
        val toInsert = mapping.candidates[nextIndex]
        
        // エディタに文字列を挿入
        WriteCommandAction.runWriteCommandAction(editor.project) {
            val document = editor.document
            val caretModel = editor.caretModel
            val offset = caretModel.offset
            
            // 前回挿入した文字列を削除（タイムアウトしていない場合）
            if (!isTimeout && keyState.lastInserted != null && shouldReplacePrevious(editor, keyState)) {
                val deleteLength = keyState.lastInserted.length
                val deleteStart = offset - deleteLength
                if (deleteStart >= 0 && deleteStart + deleteLength <= document.textLength) {
                    document.deleteString(deleteStart, offset)
                    caretModel.moveToOffset(deleteStart)
                }
            }
            
            // 新しい文字列を挿入
            document.insertString(caretModel.offset, toInsert)
            caretModel.moveToOffset(caretModel.offset + toInsert.length)
        }
        
        // 状態を更新
        keyStates[charTyped] = KeyState(
            currentIndex = nextIndex,
            lastInserted = toInsert,
            lastTypedTime = currentTime
        )
    }
    
    /**
     * 次の候補インデックスを計算
     */
    private fun calculateNextIndex(currentIndex: Int, mapping: SmartChrMapping): Int {
        val nextIndex = currentIndex + 1
        
        return when (mapping.mode) {
            CycleMode.LOOP -> nextIndex % mapping.candidates.size
            CycleMode.ONE_OF -> minOf(nextIndex, mapping.candidates.size - 1)
        }
    }
    
    /**
     * 前回挿入した文字列を置換すべきかどうかを判定
     */
    private fun shouldReplacePrevious(editor: Editor, keyState: KeyState): Boolean {
        if (keyState.lastInserted == null) return false
        
        val offset = editor.caretModel.offset
        val lastInserted = keyState.lastInserted
        val deleteStart = offset - lastInserted.length
        
        if (deleteStart < 0) return false
        
        // 前回挿入した文字列がまだ存在するか確認
        val existingText = editor.document.getText(TextRange(deleteStart, offset))
        return existingText == lastInserted
    }
    
    /**
     * キー状態をリセット
     */
    fun resetKeyStates() {
        keyStates.clear()
    }
}