# IntelliJ IDEA SmartChr Plugin

vim-smartchrプラグインの機能をIntelliJ IDEAに移植したプラグインです。同じキーを繰り返し押すことで、事前定義された文字列パターンを循環入力できます。

## 機能

- **キー循環機能**: 単一キーの連続入力で定義された文字列候補を順番に挿入・置換
- **ファイルタイプ別設定**: プログラミング言語ごとに異なるキーマッピングを設定可能
- **JSON設定**: ユーザーが直接編集可能なJSON設定ファイル
- **2つの動作モード**:
  - `LOOP`: 最後の候補の次は最初の候補に戻る
  - `ONE_OF`: 最後の候補に到達したら停止

## 使用例

```
= キーを連続で押す: = → " = " → " == " → = (ループ)
. キーをJava/Kotlinファイルで: . → -> → . (ループ)
```

## 開発環境での動作確認

### 1. プラグインのビルドと実行

```bash
./gradlew runIde
```

このコマンドでプラグインがインストールされた開発用IntelliJ IDEAが起動します。

### 2. 設定画面へのアクセス

起動したIntelliJ IDEAで：

1. **macOS**: `IntelliJ IDEA` → `Settings...` (または `Cmd + ,`)
2. **Windows/Linux**: `File` → `Settings` (または `Ctrl + Alt + S`)
3. 設定画面の左側メニューで `Editor` を展開
4. `SmartChr` を選択

### 3. JSON設定ファイルの編集

設定画面で以下の操作が可能です：

- **設定ファイルの場所確認**: JSON設定ファイルのフルパス表示
- **「設定ファイルを開く」**: クリックでJSON設定ファイルをエディタで開く
- **「設定ファイルを作成」**: デフォルトのJSON設定ファイルを作成

初期状態：
```json
{
  "mappings": []
}
```

設定例：
```json
{
  "mappings": [
    {
      "key": "=",
      "candidates": ["=", " = ", " == "],
      "mode": "LOOP",
      "fileTypes": ["*"],
      "enabled": true
    },
    {
      "key": ".",
      "candidates": [".", "->"],
      "mode": "LOOP", 
      "fileTypes": ["JAVA", "Kotlin"],
      "enabled": true
    }
  ]
}
```

### 4. 機能のテスト

JSON設定を保存後、以下で機能をテスト：

1. 新しいファイル（`.java`、`.kt`など）を作成
2. `=` キーを連続で押す → `=` → ` = ` → ` == ` → `=` と循環
3. Java/Kotlinファイルで `.` キーを連続で押す → `.` → `->` → `.` と循環

### 5. JSON設定ファイルの場所

設定ファイルは以下の場所に保存されます：

- **macOS**: `~/Library/Application Support/JetBrains/IntelliJIdea{version}/options/smartchr-mappings.json`
- **Windows**: `%APPDATA%\JetBrains\IntelliJIdea{version}\options\smartchr-mappings.json`
- **Linux**: `~/.config/JetBrains/IntelliJIdea{version}/options/smartchr-mappings.json`

## JSON設定仕様

### マッピング設定項目

| フィールド | 型 | 説明 | 必須 |
|----------|---|-----|-----|
| `key` | string | 対象のキー文字（1文字） | ✓ |
| `candidates` | string[] | 循環する文字列の配列 | ✓ |
| `mode` | string | `"LOOP"` または `"ONE_OF"` | ✓ |
| `fileTypes` | string[] | 適用するファイルタイプ（`["*"]` で全て） | ✓ |
| `enabled` | boolean | マッピングの有効/無効 | ✓ |

### ファイルタイプ例

- `"*"`: 全てのファイル
- `"JAVA"`: Javaファイル
- `"Kotlin"`: Kotlinファイル
- `"Python"`: Pythonファイル
- `"Markdown"`: Markdownファイル

## 開発情報

### 要件

- IntelliJ IDEA 2023.1以降
- Java 17以降
- Kotlin

### ビルド

```bash
# テスト実行
./gradlew test

# プラグインビルド
./gradlew buildPlugin

# 開発用IDE起動
./gradlew runIde
```

### テスト状況

全39テストが100%成功：
- JsonConfigServiceTest: 8テスト
- SmartChrSettingsTest: 8テスト  
- SmartChrSettingsComponentTest: 6テスト
- SmartChrTypedHandlerTest: 5テスト
- SmartChrIntegrationTest: 6テスト
- SmartChrModelsTest: 6テスト

### アーキテクチャ

- **JsonConfigService**: JSON設定ファイルの読み書き
- **SmartChrSettings**: 設定管理とXML/JSON移行
- **SmartChrTypedHandler**: キーストローク処理
- **SmartChrSettingsComponent**: 設定UI

## ライセンス

MIT License

## 作者

msfukui