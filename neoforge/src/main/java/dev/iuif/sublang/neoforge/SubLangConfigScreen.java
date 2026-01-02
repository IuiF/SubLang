package dev.iuif.sublang.neoforge;

import dev.iuif.sublang.config.SubLangConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class SubLangConfigScreen {

    public static Screen create(Screen parent) {
        SubLangConfig.ConfigData config = SubLangConfig.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.sublang.title"))
                .setSavingRunnable(SubLangConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General category
        ConfigCategory general = builder.getOrCreateCategory(
                Component.translatable("config.sublang.category.general"));

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.sublang.enabled"),
                        config.enabled)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.sublang.enabled.tooltip"))
                .setSaveConsumer(val -> config.enabled = val)
                .build());

        general.addEntry(entryBuilder.startEnumSelector(
                        Component.translatable("config.sublang.sourceLanguageMode"),
                        SubLangConfig.SourceLanguageMode.class,
                        config.sourceLanguageMode)
                .setDefaultValue(SubLangConfig.SourceLanguageMode.SPECIFIC_LANGUAGE)
                .setEnumNameProvider(mode -> {
                    if (mode == SubLangConfig.SourceLanguageMode.SPECIFIC_LANGUAGE) {
                        return Component.translatable("config.sublang.sourceLanguageMode.specific");
                    } else {
                        return Component.translatable("config.sublang.sourceLanguageMode.key");
                    }
                })
                .setSaveConsumer(val -> config.sourceLanguageMode = val)
                .build());

        general.addEntry(entryBuilder.startStrField(
                        Component.translatable("config.sublang.sourceLanguage"),
                        config.sourceLanguage)
                .setDefaultValue("en_us")
                .setTooltip(Component.translatable("config.sublang.sourceLanguage.tooltip"))
                .setSaveConsumer(val -> config.sourceLanguage = val)
                .build());

        general.addEntry(entryBuilder.startStrField(
                        Component.translatable("config.sublang.format"),
                        config.format)
                .setDefaultValue("{current} ({source})")
                .setTooltip(Component.translatable("config.sublang.format.tooltip"))
                .setSaveConsumer(val -> config.format = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.translatable("config.sublang.showForSame"),
                        config.showForSameTranslation)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.sublang.showForSame.tooltip"))
                .setSaveConsumer(val -> config.showForSameTranslation = val)
                .build());

        // Filter category
        ConfigCategory filter = builder.getOrCreateCategory(
                Component.translatable("config.sublang.category.filter"));

        filter.addEntry(entryBuilder.startStrList(
                        Component.translatable("config.sublang.excludedMods"),
                        config.excludedModIds)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.sublang.excludedMods.tooltip"))
                .setSaveConsumer(val -> config.excludedModIds = new ArrayList<>(val))
                .build());

        filter.addEntry(entryBuilder.startStrList(
                        Component.translatable("config.sublang.excludedItems"),
                        config.excludedItems)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.sublang.excludedItems.tooltip"))
                .setSaveConsumer(val -> config.excludedItems = new ArrayList<>(val))
                .build());

        filter.addEntry(entryBuilder.startStrList(
                        Component.translatable("config.sublang.excludedNamespaces"),
                        config.excludedNamespaces)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(Component.translatable("config.sublang.excludedNamespaces.tooltip"))
                .setSaveConsumer(val -> config.excludedNamespaces = new ArrayList<>(val))
                .build());

        return builder.build();
    }
}
