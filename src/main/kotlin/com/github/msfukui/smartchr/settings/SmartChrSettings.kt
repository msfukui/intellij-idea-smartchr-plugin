package com.github.msfukui.smartchr.settings

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.SmartChrMapping
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.XCollection

/**
 * SmartChrプラグインの設定を管理するサービス
 */
@State(
    name = "SmartChrSettings",
    storages = [Storage("smartchr.xml")]
)
class SmartChrSettings : PersistentStateComponent<SmartChrSettings.State> {
    
    private var myState = State()
    
    /**
     * 設定の永続化用データクラス
     */
    data class State(
        @XCollection(style = XCollection.Style.v2)
        var mappings: MutableList<MappingState> = mutableListOf()
    )
    
    /**
     * XMLシリアライズ用のマッピングデータ
     */
    data class MappingState(
        var key: String = "",
        var candidates: MutableList<String> = mutableListOf(),
        var mode: String = CycleMode.LOOP.name,
        var fileTypes: MutableList<String> = mutableListOf("*"),
        var enabled: Boolean = true
    )
    
    override fun getState(): State = myState
    
    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, myState)
    }
    
    /**
     * マッピングのリストを取得
     */
    fun getMappings(): List<SmartChrMapping> {
        return myState.mappings.map { state ->
            SmartChrMapping(
                key = state.key.firstOrNull() ?: ' ',
                candidates = state.candidates.toList(),
                mode = try {
                    CycleMode.valueOf(state.mode)
                } catch (e: IllegalArgumentException) {
                    CycleMode.LOOP
                },
                fileTypes = state.fileTypes.toList(),
                enabled = state.enabled
            )
        }
    }
    
    /**
     * マッピングを設定
     */
    fun setMappings(mappings: List<SmartChrMapping>) {
        myState.mappings.clear()
        myState.mappings.addAll(mappings.map { mapping ->
            MappingState(
                key = mapping.key.toString(),
                candidates = mapping.candidates.toMutableList(),
                mode = mapping.mode.name,
                fileTypes = mapping.fileTypes.toMutableList(),
                enabled = mapping.enabled
            )
        })
    }
    
    /**
     * マッピングを追加
     */
    fun addMapping(mapping: SmartChrMapping) {
        val mappingState = MappingState(
            key = mapping.key.toString(),
            candidates = mapping.candidates.toMutableList(),
            mode = mapping.mode.name,
            fileTypes = mapping.fileTypes.toMutableList(),
            enabled = mapping.enabled
        )
        myState.mappings.add(mappingState)
    }
    
    /**
     * マッピングを削除
     */
    fun removeMapping(index: Int) {
        if (index in myState.mappings.indices) {
            myState.mappings.removeAt(index)
        }
    }
    
    /**
     * 設定をリセット
     */
    fun reset() {
        myState.mappings.clear()
    }
    
    companion object {
        /**
         * インスタンスを取得
         */
        fun getInstance(): SmartChrSettings {
            return ApplicationManager.getApplication().getService(SmartChrSettings::class.java)
        }
    }
}