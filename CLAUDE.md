# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

このプロジェクトは、Vimのvim-smartchr機能をIntelliJ IDEAで実現するプラグインです。vim-smartchrは、同じキーを繰り返し押すことで、事前に定義された文字列のシーケンスを循環できる機能です（例：`=`キーを押すたびに `=`、` = `、` == ` と切り替わる）。

## 現在のプロジェクト状態

プロジェクトは初期段階にあり、IntelliJ Platform Pluginの基本構造をセットアップする必要があります。

## 開発コマンド

Gradleプロジェクトとして初期化後、以下のコマンドを使用：

```bash
# プラグインのビルド
./gradlew build

# プラグインを含むIntelliJ IDEAの起動（テスト用）
./gradlew runIde

# テストの実行
./gradlew test

# ビルド成果物のクリーン
./gradlew clean

# プラグイン配布物の作成
./gradlew buildPlugin
```

## プロジェクト構造（作成予定）

標準的なIntelliJプラグイン構造に従う：

```
├── src/
│   └── main/
│       ├── kotlin/         # Kotlinを使用する場合
│       │   └── com/example/smartchr/
│       │       ├── SmartChrAction.kt
│       │       ├── SmartChrSettings.kt
│       │       └── SmartChrConfigurable.kt
│       └── resources/
│           └── META-INF/
│               └── plugin.xml
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

## 実装の重要ポイント

1. **アクションシステム**: IntelliJのアクションシステムを使用してキーストロークを傍受し、循環動作を実装
2. **エディタAPI**: `com.intellij.openapi.editor` APIを使用してテキスト操作を行う
3. **設定画面**: `Configurable`インターフェースを実装し、ユーザーがキーマッピングを設定できるようにする
4. **パフォーマンス**: エディタの遅延を避けるため、キーストローク傍受を効率的に実装

## IntelliJ Platform Plugin要件

- **プラグイン設定**: `plugin.xml`でプラグインのメタデータ、アクション、拡張ポイントを定義
- **互換性**: `build.gradle.kts`で対応するIntelliJバージョンを指定
- **依存関係**: IntelliJ Platform SDKと必要なプラットフォームプラグインを使用

## テスト方針

- 循環ロジックの単体テスト
- IntelliJのテストフレームワークを使用した統合テスト
- サンドボックスIDEでの手動テスト（`./gradlew runIde`経由）

## 重要なAPI

- `AnAction`: アクション実装の基底クラス
- `EditorActionHandler`: エディタ固有のアクション処理
- `TypedActionHandler`: 入力文字の傍受
- `Document`: テキスト操作
- `ApplicationConfigurable`: 設定UI

## コミットメッセージ規約

このプロジェクトではConventional Commits形式を使用します：

```
<type>(<scope>): <subject>

<body>

<footer>
```

### タイプ
- `feat`: 新機能
- `fix`: バグ修正
- `docs`: ドキュメントのみの変更
- `style`: コードの意味に影響を与えない変更（空白、フォーマット、セミコロンなど）
- `refactor`: バグ修正や機能追加を伴わないコード変更
- `test`: テストの追加や修正
- `chore`: ビルドプロセスやツールの変更、ライブラリの更新など

### 例
```
feat(action): vim-smartchrの基本的なキー循環機能を実装
fix(editor): エディタでの文字置換時のカーソル位置を修正
docs: README.mdにインストール手順を追加
```

## コーディング規約

### ファイル形式
- **重要**: すべてのテキストファイルは末尾が改行のみの行で終了すること
- Gitの差分表示で "No newline at end of file" が表示されないようにする
- エディタの設定で自動的に末尾改行を追加するよう設定することを推奨
