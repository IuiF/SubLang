package dev.iuif.sublang.util;

import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.core.LanguageManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemPotion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.PotionType;

import java.util.IllegalFormatException;
import java.util.regex.Matcher;

/**
 * Resolves translation placeholders for source language.
 *
 * In 1.12.2, we don't have access to TranslatableComponent with arguments,
 * so we use heuristics to resolve placeholders for known item types (like potions).
 */
public final class TranslationResolver {

    private TranslationResolver() {
        // Utility class - prevent instantiation
    }

    /**
     * Get the source translation for an item, resolving placeholders if possible.
     *
     * @param stack The item stack
     * @param translationKey The translation key
     * @return The resolved source language string, or null if cannot resolve
     */
    public static String resolveSourceTranslation(ItemStack stack, String translationKey) {
        // Handle TRANSLATION_KEY mode - just return the key
        if (SubLangConfig.getSourceLanguageMode() == SubLangConfig.SourceLanguageMode.TRANSLATION_KEY) {
            return translationKey;
        }

        // Get the raw source translation
        String sourceFormat = LanguageManager.getSourceTranslation(translationKey);
        if (sourceFormat == null) {
            return null;
        }

        // If no placeholders, return as-is
        if (!containsPlaceholder(sourceFormat)) {
            return sourceFormat;
        }

        // Try to resolve placeholders for known item types
        String resolved = tryResolveForItem(stack, sourceFormat);
        if (resolved != null) {
            return resolved;
        }

        // Cannot resolve placeholders - skip this item
        return null;
    }

    /**
     * Check if the format string contains placeholders.
     */
    private static boolean containsPlaceholder(String format) {
        return format != null && (format.contains("%s") || format.contains("%1$s"));
    }

    /**
     * Try to resolve placeholders for specific item types.
     */
    private static String tryResolveForItem(ItemStack stack, String sourceFormat) {
        // Handle potions specially
        if (stack.getItem() instanceof ItemPotion) {
            return tryResolvePotion(stack, sourceFormat);
        }

        // Add more item type handlers here as needed

        return null;
    }

    /**
     * Resolve potion name placeholders.
     */
    private static String tryResolvePotion(ItemStack stack, String sourceFormat) {
        try {
            PotionType potionType = PotionUtils.getPotionFromItem(stack);
            if (potionType != null) {
                // Get the potion effect name in source language
                String effectKey = potionType.getNamePrefixed("effect.");
                String effectName = LanguageManager.getSourceTranslation(effectKey);

                if (effectName != null) {
                    return formatWithArgs(sourceFormat, new Object[]{effectName});
                }
            }
        } catch (Exception e) {
            // Ignore errors and return null
        }
        return null;
    }

    /**
     * Format a translation string with arguments.
     */
    private static String formatWithArgs(String format, Object[] args) {
        if (format == null || args == null || args.length == 0) {
            return format;
        }

        try {
            // Escape any % characters in string arguments
            Object[] safeArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String) {
                    safeArgs[i] = ((String) args[i]).replace("%", "%%");
                } else {
                    safeArgs[i] = args[i];
                }
            }

            return String.format(format, safeArgs);
        } catch (IllegalFormatException e) {
            return manualFormat(format, args);
        }
    }

    /**
     * Manual fallback for formatting.
     */
    private static String manualFormat(String format, Object[] args) {
        String result = format;
        int argIndex = 0;

        while (result.contains("%s") && argIndex < args.length) {
            String replacement = args[argIndex] != null ? args[argIndex].toString() : "";
            result = result.replaceFirst("%s", Matcher.quoteReplacement(replacement));
            argIndex++;
        }

        return result;
    }
}
