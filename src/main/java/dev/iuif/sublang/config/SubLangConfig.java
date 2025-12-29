package dev.iuif.sublang.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.iuif.sublang.Constants;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SubLangConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigData config = new ConfigData();

    public static class ConfigData {
        // Core settings
        public boolean enabled = true;
        public String sourceLanguage = "en_us";
        public SourceLanguageMode sourceLanguageMode = SourceLanguageMode.SPECIFIC_LANGUAGE;

        // Format settings
        public String format = "%s (%s)";
        public boolean showForSameTranslation = false;

        // Filtering
        public List<String> excludedModIds = new ArrayList<>();
        public List<String> excludedItems = new ArrayList<>();
        public List<String> excludedNamespaces = new ArrayList<>();

        // Advanced
        public boolean cacheTranslations = true;
    }

    public enum SourceLanguageMode {
        SPECIFIC_LANGUAGE,  // Use a specific language code (e.g., "en_us")
        TRANSLATION_KEY     // Show raw translation key (e.g., "item.minecraft.diamond")
    }

    public static void load() {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                config = GSON.fromJson(reader, ConfigData.class);
                Constants.LOG.info("Loaded config from {}", configPath);
            } catch (IOException e) {
                Constants.LOG.error("Failed to load config", e);
                config = new ConfigData();
            }
        } else {
            config = new ConfigData();
            save();
        }
    }

    public static void save() {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(config, writer);
            }
            Constants.LOG.debug("Saved config to {}", configPath);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }

    private static Path getConfigPath() {
        return Loader.instance().getConfigDir().toPath().resolve(Constants.MOD_ID + ".json");
    }

    // Getters
    public static boolean isEnabled() {
        return config.enabled;
    }

    public static String getSourceLanguage() {
        return config.sourceLanguage;
    }

    public static SourceLanguageMode getSourceLanguageMode() {
        return config.sourceLanguageMode;
    }

    public static String getFormat() {
        return config.format;
    }

    public static boolean showForSameTranslation() {
        return config.showForSameTranslation;
    }

    public static List<String> getExcludedModIds() {
        return config.excludedModIds;
    }

    public static List<String> getExcludedItems() {
        return config.excludedItems;
    }

    public static List<String> getExcludedNamespaces() {
        return config.excludedNamespaces;
    }

    public static boolean isCacheEnabled() {
        return config.cacheTranslations;
    }

    // Setters
    public static void setEnabled(boolean enabled) {
        config.enabled = enabled;
        save();
    }

    public static void setSourceLanguage(String sourceLanguage) {
        config.sourceLanguage = sourceLanguage;
        save();
    }

    public static void setSourceLanguageMode(SourceLanguageMode mode) {
        config.sourceLanguageMode = mode;
        save();
    }

    public static void setFormat(String format) {
        config.format = format;
        save();
    }

    public static void setShowForSameTranslation(boolean show) {
        config.showForSameTranslation = show;
        save();
    }

    public static ConfigData getConfig() {
        return config;
    }

    public static void setConfig(ConfigData newConfig) {
        config = newConfig;
        save();
    }
}
