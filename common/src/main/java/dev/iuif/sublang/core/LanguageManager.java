package dev.iuif.sublang.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.iuif.sublang.Constants;
import dev.iuif.sublang.config.SubLangConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageManager {

    private static final Map<String, Map<String, String>> languageDataCache = new ConcurrentHashMap<>();
    private static final Map<String, String> translationCache = new ConcurrentHashMap<>();

    /**
     * Get translation from source language for the given key
     */
    public static String getSourceTranslation(String key) {
        if (SubLangConfig.getSourceLanguageMode() == SubLangConfig.SourceLanguageMode.TRANSLATION_KEY) {
            return key;
        }

        String sourceLanguage = SubLangConfig.getSourceLanguage();

        // Check cache first
        if (SubLangConfig.isCacheEnabled()) {
            String cacheKey = sourceLanguage + ":" + key;
            String cached = translationCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        // Load language data if not cached
        Map<String, String> langData = getLanguageData(sourceLanguage);
        String translation = langData.getOrDefault(key, key);

        // Cache the result
        if (SubLangConfig.isCacheEnabled()) {
            String cacheKey = sourceLanguage + ":" + key;
            translationCache.put(cacheKey, translation);
        }

        return translation;
    }

    /**
     * Load language data from all resource packs for the given language code
     */
    private static Map<String, String> getLanguageData(String languageCode) {
        if (languageDataCache.containsKey(languageCode)) {
            return languageDataCache.get(languageCode);
        }

        Map<String, String> data = new HashMap<>();
        loadLanguageFromResources(languageCode, data);
        languageDataCache.put(languageCode, data);

        Constants.LOG.debug("Loaded {} translations for language {}", data.size(), languageCode);
        return data;
    }

    /**
     * Load language files from resource manager
     */
    private static void loadLanguageFromResources(String languageCode, Map<String, String> target) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        ResourceManager resourceManager = mc.getResourceManager();
        if (resourceManager == null) return;

        // Iterate over all namespaces
        for (String namespace : resourceManager.getNamespaces()) {
            Identifier langFile = Identifier.fromNamespaceAndPath(
                    namespace, "lang/" + languageCode + ".json");

            try {
                List<Resource> resources = resourceManager.getResourceStack(langFile);
                for (Resource resource : resources) {
                    try (InputStream stream = resource.open()) {
                        loadFromJson(stream, target);
                    } catch (IOException e) {
                        Constants.LOG.debug("Failed to read language file: {}", langFile);
                    }
                }
            } catch (Exception e) {
                // Language file doesn't exist for this namespace - this is normal
            }
        }
    }

    /**
     * Parse JSON language file and add entries to target map
     */
    private static void loadFromJson(InputStream stream, Map<String, String> target) {
        try {
            JsonObject json = JsonParser.parseReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    target.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        } catch (Exception e) {
            Constants.LOG.debug("Failed to parse language JSON", e);
        }
    }

    /**
     * Invalidate all caches (called on resource reload)
     */
    public static void invalidateCache() {
        languageDataCache.clear();
        translationCache.clear();
        Constants.LOG.debug("Language cache invalidated");
    }

    /**
     * Get the current game language code
     */
    public static String getCurrentLanguage() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getLanguageManager() != null) {
            return mc.getLanguageManager().getSelected();
        }
        return "en_us";
    }

    /**
     * Check if source language differs from current language
     */
    public static boolean isSourceLanguageDifferent() {
        if (SubLangConfig.getSourceLanguageMode() == SubLangConfig.SourceLanguageMode.TRANSLATION_KEY) {
            return true;  // Always show key mode as "different"
        }
        return !SubLangConfig.getSourceLanguage().equals(getCurrentLanguage());
    }
}
