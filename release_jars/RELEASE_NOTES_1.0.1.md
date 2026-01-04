# SubLang v1.0.1

## 変更内容

### 設定画面の改善
- 表示フォーマットのプレースホルダーを改善
  - `{current}` - 現在の言語（日本語など）
  - `{source}` - 元言語（英語など）
  - `\n` - 改行
- デフォルトフォーマット: `{current} ({source})`
- 従来の `%s` 形式も後方互換性のためサポート

### フォーマット例
- `{current} ({source})` → `ダイヤモンド (Diamond)`
- `{source}\n{current}` →
  ```
  Diamond
  ダイヤモンド
  ```

## 対応バージョン

| Minecraft | Loader | ファイル |
|-----------|--------|----------|
| 1.18.2 | Fabric | `sublang-fabric-1.18.2-1.0.1.jar` |
| 1.19.2 | Fabric | `sublang-fabric-1.19.2-1.0.1.jar` |
| 1.20.4 | Fabric | `sublang-fabric-1.20.4-1.0.1.jar` |
| 1.20.4 | NeoForge | `sublang-neoforge-1.20.4-1.0.1.jar` |
| 1.21 | Fabric | `sublang-fabric-1.21-1.0.1.jar` |
| 1.21 | NeoForge | `sublang-neoforge-1.21-1.0.1.jar` |
