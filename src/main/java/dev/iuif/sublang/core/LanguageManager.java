package dev.iuif.sublang.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.iuif.sublang.Constants;
import dev.iuif.sublang.config.SubLangConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        String translation = langData.get(key);

        // If translation not found, return null (skip display)
        if (translation == null) {
            return null;
        }

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
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) return;

        IResourceManager resourceManager = mc.getResourceManager();
        if (resourceManager == null) return;

        Set<String> namespaces = resourceManager.getResourceDomains();

        String jsonFileName = "lang/" + languageCode + ".json";
        String langFileName = "lang/" + languageCode + ".lang";

        // Collect all language files from all namespaces (simulating listResources)
        Map<ResourceLocation, IResource> jsonLangFiles = new LinkedHashMap<>();
        Map<ResourceLocation, IResource> langFiles = new LinkedHashMap<>();

        for (String namespace : namespaces) {
            // Try JSON format first (1.13+ style)
            ResourceLocation jsonLocation = new ResourceLocation(namespace, jsonFileName);
            try {
                List<IResource> resources = resourceManager.getAllResources(jsonLocation);
                if (!resources.isEmpty()) {
                    jsonLangFiles.put(jsonLocation, resources.get(0));
                }
            } catch (Exception e) {
                // JSON file doesn't exist, try .lang format
            }

            // Try .lang format (1.12.2 style)
            ResourceLocation langLocation = new ResourceLocation(namespace, langFileName);
            try {
                List<IResource> resources = resourceManager.getAllResources(langLocation);
                if (!resources.isEmpty()) {
                    langFiles.put(langLocation, resources.get(0));
                }
            } catch (Exception e) {
                // Language file doesn't exist for this namespace - normal
            }
        }

        Constants.LOG.debug("Found {} JSON and {} .lang files for {}",
                jsonLangFiles.size(), langFiles.size(), languageCode);

        // Load all collected JSON language files
        for (Map.Entry<ResourceLocation, IResource> entry : jsonLangFiles.entrySet()) {
            ResourceLocation location = entry.getKey();
            IResource resource = entry.getValue();

            try (InputStream stream = resource.getInputStream()) {
                loadFromJson(stream, target);
                Constants.LOG.debug("Loaded JSON language file: {}", location);
            } catch (IOException e) {
                Constants.LOG.debug("Failed to read JSON language file: {}", location);
            }
        }

        // Load all collected .lang files
        for (Map.Entry<ResourceLocation, IResource> entry : langFiles.entrySet()) {
            ResourceLocation location = entry.getKey();
            IResource resource = entry.getValue();

            try (InputStream stream = resource.getInputStream()) {
                loadFromLang(stream, target);
                Constants.LOG.debug("Loaded .lang file: {}", location);
            } catch (IOException e) {
                Constants.LOG.debug("Failed to read .lang file: {}", location);
            }
        }
    }

    /**
     * Parse JSON language file and add entries to target map
     */
    private static void loadFromJson(InputStream stream, Map<String, String> target) {
        try {
            JsonObject json = new JsonParser().parse(
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
     * Parse .lang format file (key=value format) and add entries to target map
     */
    private static void loadFromLang(InputStream stream, Map<String, String> target) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx);
                    String value = line.substring(idx + 1);
                    target.put(key, value);
                }
            }
        } catch (Exception e) {
            Constants.LOG.debug("Failed to parse .lang file", e);
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
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.getLanguageManager() != null) {
            return mc.getLanguageManager().getCurrentLanguage().getLanguageCode();
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
