# IntelliJ IDEA SmartChr Plugin

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JetBrains Plugin](https://img.shields.io/badge/JetBrains-Plugin-blue.svg)](https://plugins.jetbrains.com/)

vim-smartchrプラグインの機能をIntelliJ IDEAに移植したプラグインです。同じキーを繰り返し押すことで、事前定義された文字列パターンを循環入力できる、開発効率を向上させるツールです。

## ✨ 機能

- **🔄 キー循環機能**: 単一キーの連続入力で定義された文字列候補を順番に挿入・置換
- **📁 ファイルタイプ別設定**: プログラミング言語ごとに異なるキーマッピングを設定可能
- **⚙️ JSON設定**: ユーザーが直接編集可能なJSON設定ファイル
- **🎯 2つの動作モード**:
  - `LOOP`: 最後の候補の次は最初の候補に戻る（循環）
  - `ONE_OF`: 最後の候補に到達したら停止

## 🚀 クイックスタート

### インストール

1. IntelliJ IDEAの `Settings` → `Plugins` → `Marketplace` を開く
2. "SmartChr" を検索してインストール
3. IDEを再起動

### 基本的な使い方

```
= キーを連続で押す: = → " = " → " == " → = (ループ)
. キーをJava/Kotlinファイルで: . → -> → . (ループ)
+ キーを連続で押す: + → " + " → " += " → + (ループ)
```

## 📖 ドキュメント

- **[ユーザーガイド](docs/USER_GUIDE.md)** - 詳細なインストール・設定方法
- **[設定例集](docs/USER_GUIDE.md#実用的な設定例)** - プログラミング言語別の実用的な設定
- **[FAQ](docs/USER_GUIDE.md#よくある質問（FAQ）)** - よくある質問と解決方法

## ⭐ 対応環境

- **IntelliJ IDEA**: 2023.1 以降
- **対応言語**: Java, Kotlin, Python, JavaScript, TypeScript, HTML, CSS, Markdown など
- **OS**: Windows, macOS, Linux

## 🛠️ 開発者向け情報

### 開発環境での動作確認

```bash
# 開発用IDEの起動
./gradlew runIde

# テスト実行
./gradlew test

# プラグインビルド
./gradlew buildPlugin
```

### テスト状況

✅ **38テスト**が100%成功：
- JsonConfigService: 8テスト
- SmartChrSettings: 8テスト  
- SmartChrSettingsComponent: 5テスト
- SmartChrTypedHandler: 5テスト
- SmartChrIntegration: 6テスト
- SmartChrModels: 6テスト

### 技術スタック

- **言語**: Kotlin
- **プラットフォーム**: IntelliJ Platform
- **ビルドツール**: Gradle
- **テスト**: JUnit 4

## 🤝 コントリビューション

プロジェクトへの貢献を歓迎します！

1. プロジェクトをフォーク
2. フィーチャーブランチを作成 (`git checkout -b feature/amazing-feature`)
3. 変更をコミット (`git commit -m 'Add amazing feature'`)
4. ブランチにプッシュ (`git push origin feature/amazing-feature`)
5. プルリクエストを作成

## 📝 ライセンス

このプロジェクトは [MIT License](LICENSE) の下で公開されています。

## 👨‍💻 作者

**msfukui**

- GitHub: [@msfukui](https://github.com/msfukui)
- プロジェクト: [intellij-idea-smartchr-plugin](https://github.com/msfukui/intellij-idea-smartchr-plugin)

## 🙏 謝辞

このプラグインは [vim-smartchr](https://github.com/kana/vim-smartchr) にインスパイアされて作成されました。オリジナルの作者である kana 氏に感謝します。

---

⭐ このプロジェクトが役に立った場合は、GitHubでスターを付けていただけると嬉しいです！