package com.github.msfukui.smartchr.settings

import com.github.msfukui.smartchr.model.CycleMode
import com.github.msfukui.smartchr.model.SmartChrMapping
import com.intellij.openapi.application.PathManager
import java.io.File
import java.io.FileNotFoundException

/**
 * JSON設定ファイルの読み書きを行うサービス
 */
class JsonConfigService {
    
    /**
     * JSON設定ファイルからマッピングを読み込む
     */
    fun loadMappingsFromFile(filePath: String): List<SmartChrMapping> {
        val file = File(filePath)
        if (!file.exists()) {
            throw FileNotFoundException("Configuration file not found: $filePath")
        }
        
        val jsonContent = file.readText()
        return parseJsonToMappings(jsonContent)
    }
    
    /**
     * マッピングをJSON設定ファイルに保存する
     */
    fun saveMappingsToFile(mappings: List<SmartChrMapping>, filePath: String) {
        val jsonContent = generateJsonFromMappings(mappings)
        val file = File(filePath)
        
        // 親ディレクトリが存在しない場合は作成
        file.parentFile?.mkdirs()
        
        file.writeText(jsonContent)
    }
    
    /**
     * デフォルトの設定ファイルパスを取得
     */
    fun getDefaultConfigFilePath(): String {
        val configDir = PathManager.getOptionsPath()
        return File(configDir, "smartchr-mappings.json").absolutePath
    }
    
    /**
     * デフォルトの設定ファイルを作成
     */
    fun createDefaultConfigFile(filePath: String) {
        val defaultMappings = emptyList<SmartChrMapping>()
        saveMappingsToFile(defaultMappings, filePath)
    }
    
    /**
     * XMLベースの設定からJSONに移行
     */
    fun migrateFromXmlSettings(xmlMappings: List<SmartChrMapping>) {
        val jsonConfigPath = getDefaultConfigFilePath()
        val jsonFile = File(jsonConfigPath)
        
        // JSON設定ファイルが既に存在する場合は移行しない
        if (jsonFile.exists()) {
            return
        }
        
        // XML設定をJSON形式で保存
        if (xmlMappings.isNotEmpty()) {
            saveMappingsToFile(xmlMappings, jsonConfigPath)
        } else {
            createDefaultConfigFile(jsonConfigPath)
        }
    }
    
    /**
     * JSONテキストをマッピングリストに変換
     */
    private fun parseJsonToMappings(jsonContent: String): List<SmartChrMapping> {
        try {
            // 簡単なJSON解析（外部ライブラリを使わない）
            val mappings = mutableListOf<SmartChrMapping>()
            
            // 基本的なJSON構文チェック
            val trimmed = jsonContent.trim()
            if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
                throw IllegalArgumentException("Invalid JSON format")
            }
            
            // より厳密なJSON構文チェック
            if (!isValidJsonStructure(jsonContent)) {
                throw IllegalArgumentException("Invalid JSON structure")
            }
            
            // mappingsセクションを抽出
            val mappingsStart = jsonContent.indexOf("\"mappings\"")
            if (mappingsStart == -1) {
                return emptyList()
            }
            
            val arrayStart = jsonContent.indexOf("[", mappingsStart)
            val arrayEnd = jsonContent.lastIndexOf("]")
            if (arrayStart == -1 || arrayEnd == -1) {
                return emptyList()
            }
            
            var mappingContent = jsonContent.substring(arrayStart + 1, arrayEnd).trim()
            if (mappingContent.isEmpty()) {
                return emptyList()
            }
            
            // 各マッピングオブジェクトを解析
            var braceCount = 0
            var start = 0
            
            for (i in mappingContent.indices) {
                when (mappingContent[i]) {
                    '{' -> braceCount++
                    '}' -> {
                        braceCount--
                        if (braceCount == 0) {
                            val mappingJson = mappingContent.substring(start, i + 1).trim()
                            if (mappingJson.isNotEmpty()) {
                                val mapping = parseSingleMapping(mappingJson)
                                mappings.add(mapping)
                            }
                            // 次のオブジェクトの開始位置を探す
                            start = i + 1
                            while (start < mappingContent.length && 
                                   (mappingContent[start] == ',' || mappingContent[start].isWhitespace())) {
                                start++
                            }
                        }
                    }
                }
            }
            
            return mappings
            
        } catch (e: Exception) {
            throw IllegalArgumentException("JSON解析エラー: ${e.message}", e)
        }
    }
    
    /**
     * 単一のマッピングJSONオブジェクトを解析
     */
    private fun parseSingleMapping(mappingJson: String): SmartChrMapping {
        val key = extractJsonString(mappingJson, "key").firstOrNull() ?: ' '
        val candidates = extractJsonArray(mappingJson, "candidates")
        val mode = try {
            CycleMode.valueOf(extractJsonString(mappingJson, "mode"))
        } catch (e: Exception) {
            CycleMode.LOOP
        }
        val fileTypes = extractJsonArray(mappingJson, "fileTypes")
        val enabled = extractJsonBoolean(mappingJson, "enabled")
        
        return SmartChrMapping(
            key = key,
            candidates = candidates,
            mode = mode,
            fileTypes = fileTypes.ifEmpty { listOf("*") },
            enabled = enabled
        )
    }
    
    /**
     * JSONから文字列値を抽出
     */
    private fun extractJsonString(json: String, key: String): String {
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\""
        val regex = Regex(pattern)
        val matchResult = regex.find(json)
        return matchResult?.groupValues?.get(1) ?: ""
    }
    
    /**
     * JSONから配列を抽出
     */
    private fun extractJsonArray(json: String, key: String): List<String> {
        val pattern = "\"$key\"\\s*:\\s*\\[([^\\]]*)]"
        val regex = Regex(pattern)
        val matchResult = regex.find(json) ?: return emptyList()
        
        val arrayContent = matchResult.groupValues[1].trim()
        if (arrayContent.isEmpty()) {
            return emptyList()
        }
        
        return arrayContent.split(",")
            .map { it.trim().removeSurrounding("\"") }
            .filter { it.isNotEmpty() }
    }
    
    /**
     * JSONからブール値を抽出
     */
    private fun extractJsonBoolean(json: String, key: String): Boolean {
        val pattern = "\"$key\"\\s*:\\s*(true|false)"
        val regex = Regex(pattern)
        val matchResult = regex.find(json)
        return matchResult?.groupValues?.get(1)?.toBoolean() ?: true
    }
    
    /**
     * 基本的なJSON構造の妥当性をチェック
     */
    private fun isValidJsonStructure(jsonContent: String): Boolean {
        // 基本的なJSONキー:値のペアの存在をチェック
        // 少なくとも適切に引用符で囲まれたキーがあることを確認
        val keyValuePattern = "\"\\w+\"\\s*:"
        val regex = Regex(keyValuePattern)
        return regex.find(jsonContent) != null
    }
    
    /**
     * マッピングリストからJSONテキストを生成
     */
    private fun generateJsonFromMappings(mappings: List<SmartChrMapping>): String {
        val jsonBuilder = StringBuilder()
        jsonBuilder.appendLine("{")
        
        if (mappings.isEmpty()) {
            jsonBuilder.appendLine("  \"mappings\": []")
        } else {
            jsonBuilder.appendLine("  \"mappings\": [")
            
            mappings.forEachIndexed { index, mapping ->
                jsonBuilder.appendLine("    {")
                jsonBuilder.appendLine("      \"key\": \"${mapping.key}\",")
                
                // 候補配列
                val candidatesJson = mapping.candidates.joinToString(", ") { "\"$it\"" }
                jsonBuilder.appendLine("      \"candidates\": [$candidatesJson],")
                
                jsonBuilder.appendLine("      \"mode\": \"${mapping.mode.name}\",")
                
                // ファイルタイプ配列
                val fileTypesJson = mapping.fileTypes.joinToString(", ") { "\"$it\"" }
                jsonBuilder.appendLine("      \"fileTypes\": [$fileTypesJson],")
                
                jsonBuilder.append("      \"enabled\": ${mapping.enabled}")
                jsonBuilder.appendLine()
                
                if (index < mappings.size - 1) {
                    jsonBuilder.appendLine("    },")
                } else {
                    jsonBuilder.appendLine("    }")
                }
            }
            
            jsonBuilder.appendLine("  ]")
        }
        
        jsonBuilder.append("}")
        
        return jsonBuilder.toString()
    }
}