# Changelog

All notable changes to the IntelliJ IDEA SmartChr Plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-07-26

### 🎉 Initial Stable Release

This is the first stable release of the IntelliJ IDEA SmartChr Plugin, bringing vim-smartchr functionality to IntelliJ IDEA.

### ✨ Added

#### Core Features
- **Smart Key Cycling**: Transform single key presses into multiple string patterns
- **Two Operation Modes**: 
  - `LOOP`: Cycle back to the first candidate after the last one
  - `ONE_OF`: Stop at the last candidate
- **File Type Awareness**: Different key mappings for different programming languages
- **Wide Language Support**: Java, Kotlin, Python, JavaScript, TypeScript, HTML, CSS, Markdown, and more

#### Configuration System
- **JSON-based Configuration**: Easy-to-edit JSON settings file
- **Automatic Config Creation**: Empty configuration file created automatically when needed
- **Flexible Settings**: Per-key, per-filetype customization
- **Real-time Configuration**: Changes applied by restarting IDE

#### User Interface
- **Integrated Settings Panel**: Accessible via Settings → Editor → SmartChr
- **IntelliJ Editor Integration**: Config file opens directly in IntelliJ editor
- **Project-independent**: Works even when no project is open
- **User-friendly Design**: Clear configuration path display and easy access buttons

#### Developer Experience
- **Comprehensive Testing**: 38 tests with 100% pass rate
- **Robust Error Handling**: Graceful fallbacks and error recovery
- **Performance Optimized**: Efficient key processing with minimal overhead
- **Clean Architecture**: Well-structured codebase with clear separation of concerns

### 📖 Documentation
- **Complete User Guide**: Detailed installation and configuration instructions
- **Usage Examples**: Practical examples for different programming languages
- **FAQ Section**: Common questions and troubleshooting
- **Developer Documentation**: Build instructions and architecture overview

### 🛠️ Technical Details

#### Architecture
- **SmartChrTypedHandler**: Core key processing logic
- **JsonConfigService**: JSON configuration management
- **SmartChrSettings**: Settings persistence and migration
- **SmartChrSettingsComponent**: User interface components

#### Compatibility
- **IntelliJ IDEA**: 2023.1 and later
- **Java**: 17 and later
- **Kotlin**: Latest stable version
- **Operating Systems**: Windows, macOS, Linux

#### Testing
- **Unit Tests**: Core functionality testing
- **Integration Tests**: End-to-end workflow testing
- **UI Tests**: Settings interface testing
- **Test Coverage**: All major components covered

### 🔧 Build & Development
- **Gradle Build System**: Modern build configuration
- **GitHub Actions**: Automated CI/CD pipeline
- **Code Quality**: Consistent formatting and best practices
- **Version Management**: Semantic versioning with automated releases

---

## Planned Future Releases

### [1.1.0] - Planned
- Context-aware suggestions
- Visual feedback improvements
- Performance optimizations
- Additional language support

### [1.2.0] - Planned
- Import/export configuration
- Preset configurations for popular languages
- Advanced pattern matching
- Plugin marketplace integration

---

## Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by [vim-smartchr](https://github.com/kana/vim-smartchr) by kana
- Built with [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/)
- Community feedback and testing support