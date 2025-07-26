package com.github.msfukui.smartchr.settings

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.SmartChrMapping
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.XCollection
import java.io.File

/**
 * SmartChrプラグインの設定を管理するサービス
 */
@State(
    name = "SmartChrSettings",
    storages = [Storage("smartchr.xml")]
)
class SmartChrSettings : PersistentStateComponent<SmartChrSettings.State> {
    
    private var myState = State()
    private val jsonConfigService = JsonConfigService()
    private var isJsonMigrated = false
    
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
        // XML設定からJSON設定への移行を実行
        ensureJsonMigration()
        
        // JSON設定ファイルから読み込み
        val jsonConfigPath = jsonConfigService.getDefaultConfigFilePath()
        val jsonFile = File(jsonConfigPath)
        
        return if (jsonFile.exists()) {
            try {
                jsonConfigService.loadMappingsFromFile(jsonConfigPath)
            } catch (e: Exception) {
                // JSON読み込みに失敗した場合はXML設定を使用
                getXmlMappings()
            }
        } else {
            // JSON設定ファイルが存在しない場合はXML設定を使用
            getXmlMappings()
        }
    }
    
    /**
     * XML設定からマッピングを取得（後方互換性のため）
     */
    private fun getXmlMappings(): List<SmartChrMapping> {
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
        // JSON設定ファイルに保存
        val jsonConfigPath = jsonConfigService.getDefaultConfigFilePath()
        try {
            jsonConfigService.saveMappingsToFile(mappings, jsonConfigPath)
        } catch (e: Exception) {
            // JSON保存に失敗した場合は元の方法で保存
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
    }
    
    /**
     * マッピングを追加
     */
    fun addMapping(mapping: SmartChrMapping) {
        val currentMappings = getMappings().toMutableList()
        currentMappings.add(mapping)
        setMappings(currentMappings)
    }
    
    /**
     * マッピングを削除
     */
    fun removeMapping(index: Int) {
        val currentMappings = getMappings().toMutableList()
        if (index in currentMappings.indices) {
            currentMappings.removeAt(index)
            setMappings(currentMappings)
        }
    }
    
    /**
     * 設定をリセット
     */
    fun reset() {
        val jsonConfigPath = jsonConfigService.getDefaultConfigFilePath()
        try {
            jsonConfigService.createDefaultConfigFile(jsonConfigPath)
        } catch (e: Exception) {
            // JSON操作に失敗した場合は元の方法でリセット
            myState.mappings.clear()
        }
    }
    
    /**
     * XMLからJSONへの移行を確実に実行
     */
    private fun ensureJsonMigration() {
        if (!isJsonMigrated) {
            val xmlMappings = getXmlMappings()
            jsonConfigService.migrateFromXmlSettings(xmlMappings)
            isJsonMigrated = true
        }
    }
    
    /**
     * JSON設定ファイルのパスを取得
     */
    fun getJsonConfigFilePath(): String {
        return jsonConfigService.getDefaultConfigFilePath()
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