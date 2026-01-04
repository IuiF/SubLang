package dev.iuif.sublang.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Safe format string processor for bilingual name formatting.
 *
 * Features:
 * - Named placeholders: {current}, {source}
 * - Case-insensitive: {Current}, {SOURCE}, {CuRrEnT}
 * - Legacy %s format support
 * - Mixed format: {current} (%s)
 * - Safe handling of % characters in translations
 * - Graceful error handling
 */
public final class FormatStringProcessor {

    // Pre-compiled patterns for performance
    private static final Pattern CURRENT_PATTERN =
        Pattern.compile("\\{current\\}", Pattern.CASE_INSENSITIVE);
    private static final Pattern SOURCE_PATTERN =
        Pattern.compile("\\{source\\}", Pattern.CASE_INSENSITIVE);

    // Default format used when input is invalid
    private static final String DEFAULT_FORMAT = "{current} ({source})";

    private FormatStringProcessor() {
        // Utility class - prevent instantiation
    }

    /**
     * Process format string with current and source translations.
     *
     * @param format The format string (may contain {current}, {source}, %s)
     * @param current The current language translation
     * @param source The source language translation
     * @return Formatted string, never null
     */
    public static String format(String format, String current, String source) {
        // Null/empty safety
        if (format == null || format.isEmpty()) {
            format = DEFAULT_FORMAT;
        }
        if (current == null) {
            current = "";
        }
        if (source == null) {
            source = "";
        }

        String result = format;

        // Track if named placeholders were used
        boolean hasNamedPlaceholders = containsNamedPlaceholder(result);

        // Step 1: Replace named placeholders (case-insensitive)
        result = replaceNamedPlaceholders(result, current, source);

        // Step 2: Handle legacy %s format (only if %s remains in string)
        if (result.contains("%s")) {
            result = replaceLegacyFormat(result, current, source, hasNamedPlaceholders);
        }

        // Step 3: Handle newline escape sequence
        result = result.replace("\\n", "\n");

        return result;
    }

    /**
     * Check if format string contains named placeholders
     */
    private static boolean containsNamedPlaceholder(String format) {
        return CURRENT_PATTERN.matcher(format).find() ||
               SOURCE_PATTERN.matcher(format).find();
    }

    /**
     * Replace {current} and {source} placeholders (case-insensitive)
     */
    private static String replaceNamedPlaceholders(String format, String current, String source) {
        String result = CURRENT_PATTERN.matcher(format)
            .replaceAll(Matcher.quoteReplacement(current));
        result = SOURCE_PATTERN.matcher(result)
            .replaceAll(Matcher.quoteReplacement(source));
        return result;
    }

    /**
     * Replace %s placeholders with proper escaping
     */
    private static String replaceLegacyFormat(String format, String current, String source,
                                               boolean namedPlaceholdersUsed) {
        // Count remaining %s placeholders
        int count = countOccurrences(format, "%s");

        if (count == 0) {
            return format;
        }

        // Escape % characters in values to prevent format exceptions
        String safeCurrent = escapePercentForFormat(current);
        String safeSource = escapePercentForFormat(source);

        try {
            if (count == 1) {
                // Single %s - if named placeholders were used for current,
                // this is for source only
                return String.format(format, safeSource);
            } else if (count == 2) {
                // Two %s placeholders: first=current, second=source
                return String.format(format, safeCurrent, safeSource);
            } else {
                // More than 2 %s - provide first two values
                Object[] args = new Object[count];
                args[0] = safeCurrent;
                args[1] = safeSource;
                for (int i = 2; i < count; i++) {
                    args[i] = "";  // Fill extra placeholders with empty string
                }
                return String.format(format, args);
            }
        } catch (Exception e) {
            // Fallback: manual replacement if String.format fails
            return manualReplace(format, current, source);
        }
    }

    /**
     * Escape % characters for use in String.format
     * (% becomes %% to be treated as literal)
     */
    private static String escapePercentForFormat(String value) {
        if (value == null) return "";
        return value.replace("%", "%%");
    }

    /**
     * Count occurrences of a substring
     */
    private static int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * Manual string replacement fallback when String.format fails
     */
    private static String manualReplace(String format, String current, String source) {
        String result = format;
        // Replace first %s with current
        int firstIdx = result.indexOf("%s");
        if (firstIdx != -1) {
            result = result.substring(0, firstIdx) + current +
                     result.substring(firstIdx + 2);
        }
        // Replace second %s with source
        int secondIdx = result.indexOf("%s");
        if (secondIdx != -1) {
            result = result.substring(0, secondIdx) + source +
                     result.substring(secondIdx + 2);
        }
        return result;
    }
}
