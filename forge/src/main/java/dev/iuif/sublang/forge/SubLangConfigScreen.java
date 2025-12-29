package dev.iuif.sublang.forge;

import dev.iuif.sublang.config.SubLangConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;

public class SubLangConfigScreen {

    public static Screen create(Screen parent) {
        SubLangConfig.ConfigData config = SubLangConfig.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslationTextComponent("config.sublang.title"))
                .setSavingRunnable(SubLangConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General category
        ConfigCategory general = builder.getOrCreateCategory(
                new TranslationTextComponent("config.sublang.category.general"));

        general.addEntry(entryBuilder.startBooleanToggle(
                        new TranslationTextComponent("config.sublang.enabled"),
                        config.enabled)
                .setDefaultValue(true)
                .setTooltip(new TranslationTextComponent("config.sublang.enabled.tooltip"))
                .setSaveConsumer(val -> config.enabled = val)
                .build());

        general.addEntry(entryBuilder.startEnumSelector(
                        new TranslationTextComponent("config.sublang.sourceLanguageMode"),
                        SubLangConfig.SourceLanguageMode.class,
                        config.sourceLanguageMode)
                .setDefaultValue(SubLangConfig.SourceLanguageMode.SPECIFIC_LANGUAGE)
                .setEnumNameProvider(mode -> {
                    if (mode == SubLangConfig.SourceLanguageMode.SPECIFIC_LANGUAGE) {
                        return new TranslationTextComponent("config.sublang.sourceLanguageMode.specific");
                    } else {
                        return new TranslationTextComponent("config.sublang.sourceLanguageMode.key");
                    }
                })
                .setSaveConsumer(val -> config.sourceLanguageMode = val)
                .build());

        general.addEntry(entryBuilder.startStrField(
                        new TranslationTextComponent("config.sublang.sourceLanguage"),
                        config.sourceLanguage)
                .setDefaultValue("en_us")
                .setTooltip(new TranslationTextComponent("config.sublang.sourceLanguage.tooltip"))
                .setSaveConsumer(val -> config.sourceLanguage = val)
                .build());

        general.addEntry(entryBuilder.startStrField(
                        new TranslationTextComponent("config.sublang.format"),
                        config.format)
                .setDefaultValue("%s (%s)")
                .setTooltip(new TranslationTextComponent("config.sublang.format.tooltip"))
                .setSaveConsumer(val -> config.format = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        new TranslationTextComponent("config.sublang.showForSame"),
                        config.showForSameTranslation)
                .setDefaultValue(false)
                .setTooltip(new TranslationTextComponent("config.sublang.showForSame.tooltip"))
                .setSaveConsumer(val -> config.showForSameTranslation = val)
                .build());

        // Filter category
        ConfigCategory filter = builder.getOrCreateCategory(
                new TranslationTextComponent("config.sublang.category.filter"));

        filter.addEntry(entryBuilder.startStrList(
                        new TranslationTextComponent("config.sublang.excludedMods"),
                        config.excludedModIds)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(new TranslationTextComponent("config.sublang.excludedMods.tooltip"))
                .setSaveConsumer(val -> config.excludedModIds = new ArrayList<>(val))
                .build());

        filter.addEntry(entryBuilder.startStrList(
                        new TranslationTextComponent("config.sublang.excludedItems"),
                        config.excludedItems)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(new TranslationTextComponent("config.sublang.excludedItems.tooltip"))
                .setSaveConsumer(val -> config.excludedItems = new ArrayList<>(val))
                .build());

        filter.addEntry(entryBuilder.startStrList(
                        new TranslationTextComponent("config.sublang.excludedNamespaces"),
                        config.excludedNamespaces)
                .setDefaultValue(new ArrayList<>())
                .setTooltip(new TranslationTextComponent("config.sublang.excludedNamespaces.tooltip"))
                .setSaveConsumer(val -> config.excludedNamespaces = new ArrayList<>(val))
                .build());

        return builder.build();
    }
}
