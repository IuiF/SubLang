package dev.iuif.sublang;

import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.core.LanguageManager;
import dev.iuif.sublang.platform.Services;

public class SubLangMod {

    public static void init() {
        Constants.LOG.info("SubLang initializing on {}!", Services.PLATFORM.getPlatformName());

        // Load configuration
        SubLangConfig.load();

        Constants.LOG.info("SubLang initialized. Source language: {}", SubLangConfig.getSourceLanguage());
    }

    public static void onResourcesReload() {
        // Called when resources are reloaded (language change, resource pack change)
        Constants.LOG.debug("Resources reloaded, refreshing source language data");
        LanguageManager.invalidateCache();
    }
}
