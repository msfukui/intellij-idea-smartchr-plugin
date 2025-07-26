# IntelliJ IDEA SmartChr Plugin - ユーザーガイド

## 概要

SmartChrプラグインは、vim-smartchrの機能をIntelliJ IDEAに移植したプラグインです。同じキーを繰り返し押すことで、事前定義された文字列パターンを循環入力できる便利な機能を提供します。

## インストール方法

### JetBrains Marketplace からのインストール（推奨）

1. IntelliJ IDEAを開く
2. `File` → `Settings` (Windows/Linux) または `IntelliJ IDEA` → `Preferences` (macOS) を選択
3. 左側のメニューから `Plugins` を選択
4. `Marketplace` タブで "SmartChr" を検索
5. `Install` ボタンをクリック
6. IntelliJ IDEAを再起動

### 手動インストール

1. [リリースページ](https://github.com/msfukui/intellij-idea-smartchr-plugin/releases) からプラグインファイル (`.zip`) をダウンロード
2. IntelliJ IDEAを開く
3. `File` → `Settings` → `Plugins` を選択
4. 設定アイコン（歯車） → `Install Plugin from Disk...` を選択
5. ダウンロードしたファイルを選択
6. IntelliJ IDEAを再起動

## 基本的な使い方

### 1. 初期設定

プラグインをインストール後、設定ファイルを作成する必要があります：

1. `File` → `Settings` → `Editor` → `SmartChr` を開く
2. 「設定ファイルを開く」ボタンをクリック
3. 空のJSON設定ファイルが自動作成され、エディタで開かれます

### 2. 基本的な設定例

以下の設定をコピーして設定ファイルに貼り付けてください：

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
    },
    {
      "key": "+",
      "candidates": ["+", " + ", " += "],
      "mode": "LOOP",
      "fileTypes": ["*"],
      "enabled": true
    }
  ]
}
```

### 3. 使用方法

設定完了後、以下のように使用できます：

1. **Java/Kotlinファイルで**：
   - `.` キーを連続で押す → `.` → `->` → `.` → `->` ... (循環)

2. **すべてのファイルで**：
   - `=` キーを連続で押す → `=` → ` = ` → ` == ` → `=` ... (循環)
   - `+` キーを連続で押す → `+` → ` + ` → ` += ` → `+` ... (循環)

## 設定ファイルの詳細

### JSON設定ファイルの構造

```json
{
  "mappings": [
    {
      "key": "対象キー（1文字）",
      "candidates": ["候補1", "候補2", "候補3"],
      "mode": "LOOP または ONE_OF",
      "fileTypes": ["適用するファイルタイプ"],
      "enabled": true
    }
  ]
}
```

### 設定項目の説明

| 項目 | 型 | 説明 | 例 |
|------|---|------|-----|
| `key` | string | 対象のキー文字（1文字のみ） | `"="`, `"."`, `"+"` |
| `candidates` | array | 循環する文字列の配列 | `["=", " = ", " == "]` |
| `mode` | string | 動作モード（詳細は下記参照） | `"LOOP"`, `"ONE_OF"` |
| `fileTypes` | array | 適用するファイルタイプ | `["*"]`, `["JAVA", "Kotlin"]` |
| `enabled` | boolean | マッピングの有効/無効 | `true`, `false` |

### 動作モード

- **LOOP**: 最後の候補に到達したら最初の候補に戻る（循環）
- **ONE_OF**: 最後の候補に到達したら停止

### ファイルタイプ

- `"*"`: すべてのファイルタイプに適用
- `"JAVA"`: Javaファイル (`.java`)
- `"Kotlin"`: Kotlinファイル (`.kt`)
- `"Python"`: Pythonファイル (`.py`)
- `"JavaScript"`: JavaScriptファイル (`.js`)
- `"TypeScript"`: TypeScriptファイル (`.ts`)
- `"HTML"`: HTMLファイル (`.html`)
- `"CSS"`: CSSファイル (`.css`)
- `"Markdown"`: Markdownファイル (`.md`)

## 実用的な設定例

### プログラミング言語別の設定

```json
{
  "mappings": [
    {
      "key": "=",
      "candidates": ["=", " = ", " == ", " === "],
      "mode": "LOOP",
      "fileTypes": ["JavaScript", "TypeScript"],
      "enabled": true
    },
    {
      "key": "=",
      "candidates": ["=", " = ", " == "],
      "mode": "LOOP",
      "fileTypes": ["JAVA", "Kotlin", "Python"],
      "enabled": true
    },
    {
      "key": "-",
      "candidates": ["-", " - ", " -> ", " => "],
      "mode": "LOOP",
      "fileTypes": ["JavaScript", "TypeScript"],
      "enabled": true
    },
    {
      "key": "<",
      "candidates": ["<", " < ", " <= ", " << "],
      "mode": "LOOP",
      "fileTypes": ["*"],
      "enabled": true
    }
  ]
}
```

### HTML/CSS向けの設定

```json
{
  "mappings": [
    {
      "key": ":",
      "candidates": [":", ": "],
      "mode": "LOOP",
      "fileTypes": ["CSS"],
      "enabled": true
    },
    {
      "key": "<",
      "candidates": ["<", "</"],
      "mode": "ONE_OF",
      "fileTypes": ["HTML"],
      "enabled": true
    }
  ]
}
```

## 設定ファイルの場所

設定ファイルは以下の場所に保存されます：

- **Windows**: `%APPDATA%\JetBrains\IntelliJIdea{version}\options\smartchr-mappings.json`
- **macOS**: `~/Library/Application Support/JetBrains/IntelliJIdea{version}/options/smartchr-mappings.json`
- **Linux**: `~/.config/JetBrains/IntelliJIdea{version}/options/smartchr-mappings.json`

## よくある質問（FAQ）

### Q: 設定ファイルを編集したが反映されない

A: IntelliJ IDEAを再起動してください。設定ファイルの変更は再起動後に反映されます。

### Q: 特定のファイルタイプでのみ機能を無効にしたい

A: 該当するマッピングの `enabled` を `false` に設定するか、`fileTypes` から対象のファイルタイプを除外してください。

### Q: キーを押してもSmartChrが動作しない

A: 以下を確認してください：
1. プラグインが有効になっているか
2. 設定ファイルの構文が正しいか（JSON形式）
3. `enabled` が `true` になっているか
4. 現在のファイルタイプが `fileTypes` に含まれているか

### Q: カスタムファイルタイプを追加したい

A: IntelliJが認識するファイルタイプ名を使用してください。不明な場合は、`File` → `Settings` → `Editor` → `File Types` でファイルタイプ名を確認できます。

## トラブルシューティング

### 設定ファイルが開けない

1. IntelliJ IDEAの設定画面（`Settings` → `Editor` → `SmartChr`）を開く
2. 設定ファイルのパスが表示されていることを確認
3. ファイルが存在しない場合は「設定ファイルを開く」ボタンをクリックして自動作成

### JSON構文エラー

設定ファイルのJSON構文が間違っている場合、プラグインが正常に動作しません。以下を確認してください：

- すべての文字列が二重引用符で囲まれているか
- 配列やオブジェクトの要素間にカンマがあるか
- 括弧の対応が正しいか

オンラインのJSON検証ツールを使用して構文を確認することをお勧めします。

### プラグインが認識されない

1. `File` → `Settings` → `Plugins` でプラグインが有効になっていることを確認
2. IntelliJ IDEAを再起動
3. それでも解決しない場合は、プラグインを一度無効にして再有効化

## サポート

- **バグレポート**: [GitHub Issues](https://github.com/msfukui/intellij-idea-smartchr-plugin/issues)
- **機能リクエスト**: [GitHub Issues](https://github.com/msfukui/intellij-idea-smartchr-plugin/issues)
- **プロジェクトページ**: [GitHub](https://github.com/msfukui/intellij-idea-smartchr-plugin)

## ライセンス

MIT License

---

このドキュメントに関する質問や改善提案がございましたら、GitHubのIssueページにお寄せください。