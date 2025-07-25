package com.github.msfukui.smartchr.model

/**
 * キー循環の動作モード
 */
enum class CycleMode {
    /**
     * 最後の候補で停止（循環しない）
     */
    ONE_OF,
    
    /**
     * 最後の候補の次は最初に戻る（循環する）
     */
    LOOP
}

/**
 * SmartChrのキーマッピング設定
 */
data class SmartChrMapping(
    /**
     * トリガーとなるキー文字
     */
    val key: Char,
    
    /**
     * 循環する文字列候補のリスト
     */
    val candidates: List<String>,
    
    /**
     * 循環モード
     */
    val mode: CycleMode = CycleMode.LOOP,
    
    /**
     * 有効なファイルタイプのリスト（"*"は全てのファイルタイプ）
     */
    val fileTypes: List<String> = listOf("*"),
    
    /**
     * マッピングが有効かどうか
     */
    val enabled: Boolean = true
) {
    init {
        require(candidates.isNotEmpty()) { "Candidates list must not be empty" }
    }
}

/**
 * キー入力の状態を管理するクラス
 */
data class KeyState(
    /**
     * 現在の候補インデックス
     */
    val currentIndex: Int = 0,
    
    /**
     * 最後に挿入された文字列
     */
    val lastInserted: String? = null,
    
    /**
     * 最後の入力時刻（ミリ秒）
     */
    val lastTypedTime: Long = 0
)