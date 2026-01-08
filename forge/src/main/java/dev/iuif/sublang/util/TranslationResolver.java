package dev.iuif.sublang.util;

import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.core.LanguageManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.IllegalFormatException;
import java.util.regex.Matcher;

/**
 * Resolves translatable components to their source language equivalents.
 *
 * This handles the case where translations contain placeholders (e.g., "Potion of %s")
 * by extracting arguments from TranslationTextComponent and applying them to the
 * source language format string.
 */
public final class TranslationResolver {

    private static final int MAX_RECURSION_DEPTH = 10;

    private TranslationResolver() {
        // Utility class - prevent instantiation
    }

    /**
     * Resolve a component to its source language equivalent.
     *
     * @param component The component to resolve
     * @return The resolved source language string, or null if resolution fails
     */
    public static String resolveToSourceLanguage(ITextComponent component) {
        return resolveToSourceLanguage(component, 0);
    }

    private static String resolveToSourceLanguage(ITextComponent component, int depth) {
        if (component == null || depth > MAX_RECURSION_DEPTH) {
            return null;
        }

        if (component instanceof TranslationTextComponent) {
            return resolveTranslationTextComponent((TranslationTextComponent) component, depth);
        }

        // For non-translatable content (StringTextComponent, etc.),
        // return the string representation as-is
        return component.getString();
    }

    /**
     * Resolve a TranslationTextComponent to its source language equivalent.
     */
    private static String resolveTranslationTextComponent(TranslationTextComponent component, int depth) {
        String key = component.getKey();
        Object[] args = component.getArgs();

        // Handle TRANSLATION_KEY mode - just return the key
        if (SubLangConfig.getSourceLanguageMode() == SubLangConfig.SourceLanguageMode.TRANSLATION_KEY) {
            return key;
        }

        // Get source translation format string
        String sourceFormat = LanguageManager.getSourceTranslation(key);
        if (sourceFormat == null) {
            // No translation found, return key as fallback
            return key;
        }

        // If no args, return the source format directly
        if (args == null || args.length == 0) {
            return sourceFormat;
        }

        // Resolve arguments recursively (they may be Components themselves)
        Object[] resolvedArgs = resolveArguments(args, depth);

        // Format the source string with resolved args
        return formatWithArgs(sourceFormat, resolvedArgs);
    }

    /**
     * Resolve an array of arguments, converting Component arguments to their
     * source language string equivalents.
     */
    private static Object[] resolveArguments(Object[] args, int depth) {
        Object[] resolved = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg instanceof ITextComponent) {
                // Recursively resolve Component arguments
                String resolvedStr = resolveToSourceLanguage((ITextComponent) arg, depth + 1);
                resolved[i] = resolvedStr != null ? resolvedStr : ((ITextComponent) arg).getString();
            } else {
                // Keep primitives (String, Number, Boolean) as-is
                resolved[i] = arg;
            }
        }

        return resolved;
    }

    /**
     * Format a translation string with arguments, handling both %s and indexed
     * placeholders (%1$s, %2$s, etc.).
     */
    private static String formatWithArgs(String format, Object[] args) {
        if (format == null || args == null || args.length == 0) {
            return format;
        }

        try {
            // Escape any % characters in string arguments to prevent format exceptions
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
            // If formatting fails, try manual replacement
            return manualFormat(format, args);
        }
    }

    /**
     * Manual fallback for formatting when String.format fails.
     * Handles simple %s placeholders sequentially.
     */
    private static String manualFormat(String format, Object[] args) {
        String result = format;
        int argIndex = 0;

        // Replace %s placeholders one by one
        while (result.contains("%s") && argIndex < args.length) {
            String replacement = args[argIndex] != null ? args[argIndex].toString() : "";
            result = result.replaceFirst("%s", Matcher.quoteReplacement(replacement));
            argIndex++;
        }

        return result;
    }
}
