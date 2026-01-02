package dev.iuif.sublang.core;

import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.util.TranslationKeyExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;

public class NameFormatter {

    /**
     * Format item name with source language translation appended
     *
     * @param stack The item stack
     * @param currentName The current (translated) name component
     * @return Formatted component with source translation, or original if not applicable
     */
    public static Component formatItemName(ItemStack stack, Component currentName) {
        // Check if mod is enabled
        if (!SubLangConfig.isEnabled()) {
            return currentName;
        }

        // Check if source language is different from current
        if (!LanguageManager.isSourceLanguageDifferent()) {
            return currentName;
        }

        // Get the translation key for this item
        String translationKey = stack.getItem().getDescriptionId();

        // Check exclusions
        if (TranslationKeyExtractor.isExcluded(translationKey, stack)) {
            return currentName;
        }

        // Get source translation
        String sourceTranslation = LanguageManager.getSourceTranslation(translationKey);

        // Skip if no translation found
        if (sourceTranslation == null) {
            return currentName;
        }

        String currentTranslation = currentName.getString();

        // Skip if same translation and config says not to show
        if (sourceTranslation.equals(currentTranslation) && !SubLangConfig.showForSameTranslation()) {
            return currentName;
        }

        // Format the bilingual name
        return formatBilingual(currentName, sourceTranslation);
    }

    /**
     * Format bilingual name based on config format string
     */
    private static Component formatBilingual(Component currentName, String sourceTranslation) {
        String format = SubLangConfig.getFormat();
        String current = currentName.getString();

        // Apply format - support both new named placeholders and legacy %s format
        String formatted;
        if (format.contains("{current}") || format.contains("{source}")) {
            // New named placeholder format
            formatted = format
                .replace("{current}", current)
                .replace("{source}", sourceTranslation);
        } else {
            // Legacy %s format (backward compatibility)
            formatted = String.format(format, current, sourceTranslation);
        }

        // Support newline escape sequence
        formatted = formatted.replace("\\n", "\n");

        // Preserve original styling
        Style style = currentName.getStyle();
        MutableComponent result = new TextComponent(formatted);

        if (style != null && !style.equals(Style.EMPTY)) {
            result = result.withStyle(style);
        }

        return result;
    }

    /**
     * Format with separate styling for source translation (gray color)
     * Alternative formatting that preserves original style and adds styled source
     */
    public static Component formatWithStyledSource(Component currentName, String sourceTranslation) {
        MutableComponent result = TextComponent.EMPTY.copy();
        result.append(currentName);
        result.append(new TextComponent(" ("));
        result.append(new TextComponent(sourceTranslation).withStyle(style -> style.withColor(0x888888)));
        result.append(new TextComponent(")"));
        return result;
    }
}
