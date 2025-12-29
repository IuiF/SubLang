package dev.iuif.sublang.util;

import dev.iuif.sublang.config.SubLangConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TranslationKeyExtractor {

    /**
     * Extract mod ID from translation key
     * Format: "type.modid.name" -> "modid"
     */
    public static String extractModId(String translationKey) {
        String[] parts = translationKey.split("\\.");
        if (parts.length >= 2) {
            return parts[1];
        }
        return "minecraft";
    }

    /**
     * Get the item ID (namespace:path) from an ItemStack
     */
    public static String getItemId(ItemStack stack) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null ? key.toString() : "";
    }

    /**
     * Get the namespace from an ItemStack
     */
    public static String getNamespace(ItemStack stack) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null ? key.getNamespace() : "minecraft";
    }

    /**
     * Check if the item should be excluded based on config
     */
    public static boolean isExcluded(String translationKey, ItemStack stack) {
        // Check mod ID exclusion
        String modId = extractModId(translationKey);
        if (SubLangConfig.getExcludedModIds().contains(modId)) {
            return true;
        }

        // Check namespace exclusion
        String namespace = getNamespace(stack);
        if (SubLangConfig.getExcludedNamespaces().contains(namespace)) {
            return true;
        }

        // Check item ID exclusion
        String itemId = getItemId(stack);
        if (SubLangConfig.getExcludedItems().contains(itemId)) {
            return true;
        }

        return false;
    }

    /**
     * Check if a translation key is for items/blocks that should be modified
     */
    public static boolean isItemOrBlockKey(String key) {
        return key.startsWith("item.") ||
               key.startsWith("block.") ||
               key.startsWith("entity.") ||
               key.startsWith("effect.") ||
               key.startsWith("enchantment.") ||
               key.startsWith("potion.") ||
               key.startsWith("biome.") ||
               key.startsWith("advancement.");
    }
}
