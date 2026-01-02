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
| フォーマット | 表示結果 |
|-------------|---------|
| `{current} ({source})` | `ダイヤモンド (Diamond)` |
| `{source}\n{current}` | `Diamond`<br>`ダイヤモンド` |
| `{source} - {current}` | `Diamond - ダイヤモンド` |

## 対応バージョン
- Minecraft 1.20.4
- Fabric / NeoForge

## ダウンロード
- `sublang-fabric-1.20.4-1.0.1.jar` - Fabric版
- `sublang-neoforge-1.20.4-1.0.1.jar` - NeoForge版
