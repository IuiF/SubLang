package dev.iuif.sublang.core;

import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.util.TranslationKeyExtractor;
import net.minecraft.item.ItemStack;

public class NameFormatter {

    /**
     * Get bilingual suffix to append to item name (for tooltip modification)
     * Returns the suffix like " (English Name)" or null if not applicable
     *
     * @param stack The item stack
     * @return Suffix string to append, or null if no modification needed
     */
    public static String getBilingualSuffix(ItemStack stack) {
        // Check if mod is enabled
        if (!SubLangConfig.isEnabled()) {
            return null;
        }

        // Check if source language is different from current
        if (!LanguageManager.isSourceLanguageDifferent()) {
            return null;
        }

        // Get the translation key for this item
        String translationKey = stack.getItem().getTranslationKey() + ".name";

        // Check exclusions
        if (TranslationKeyExtractor.isExcluded(translationKey, stack)) {
            return null;
        }

        // Get source translation
        String sourceTranslation = LanguageManager.getSourceTranslation(translationKey);
        String currentTranslation = stack.getDisplayName();

        // Skip if same translation and config says not to show
        if (sourceTranslation.equals(currentTranslation) && !SubLangConfig.showForSameTranslation()) {
            return null;
        }

        // Format: extract suffix from format string
        // Default format is "%s (%s)" which means suffix should be " (sourceTranslation)"
        String format = SubLangConfig.getFormat();

        // Calculate what suffix to add
        // If format is "%s (%s)", the suffix is " (" + sourceTranslation + ")"
        String fullFormatted = String.format(format, currentTranslation, sourceTranslation);
        if (fullFormatted.startsWith(currentTranslation)) {
            return fullFormatted.substring(currentTranslation.length());
        }

        // Fallback: just add " (sourceTranslation)"
        return " (" + sourceTranslation + ")";
    }

    /**
     * Format bilingual name based on config format string
     * Returns the fully formatted string
     */
    public static String formatBilingual(String currentName, String sourceTranslation) {
        String format = SubLangConfig.getFormat();
        return String.format(format, currentName, sourceTranslation);
    }
}
