# SubLang v1.0.3

## 変更内容

### バグ修正: プレースホルダー翻訳の解決

翻訳テキストにプレースホルダー（`%s`や`%1$s`）が含まれている場合に、正しいソース言語の翻訳が表示されるようになりました。

**修正前:**
- 表示: "回復のポーション (Potion of %s)" - プレースホルダーが未解決

**修正後:**
- 表示: "回復のポーション (Potion of Healing)" - プレースホルダーが正しく解決

### 技術的な変更

- `TranslationResolver` クラスを追加
  - `TranslatableContents`/`TranslatableComponent`から引数を抽出
  - ソース言語のフォーマット文字列に引数を適用
  - ネストされたComponent引数を再帰的に解決

### 影響を受けるアイテム

- ポーション (Potions)
- スプラッシュポーション (Splash Potions)
- 残留ポーション (Lingering Potions)
- エンチャントの本 (Enchanted Books)
- その他プレースホルダーを使用するアイテム

## 対応バージョン

| Minecraft | Loader | ファイル |
|-----------|--------|----------|
| 1.12.2 | Forge | `sublang-1.12.2-1.0.3.jar` |
| 1.16.5 | Forge | `sublang-1.16.5-1.0.3.jar` |
| 1.18.2 | Fabric | `sublang-fabric-1.18.2-1.0.3.jar` |
| 1.19.2 | Fabric | `sublang-fabric-1.19.2-1.0.3.jar` |
| 1.20.4 | Fabric | `sublang-fabric-1.20.4-1.0.3.jar` |
| 1.20.4 | NeoForge | `sublang-neoforge-1.20.4-1.0.3.jar` |
| 1.21 | Fabric | `sublang-fabric-1.21-1.0.3.jar` |
| 1.21 | NeoForge | `sublang-neoforge-1.21-1.0.3.jar` |
