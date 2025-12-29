package dev.iuif.sublang.forge;

import dev.iuif.sublang.Constants;
import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.core.LanguageManager;
import dev.iuif.sublang.core.NameFormatter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Constants.MOD_ID, name = Constants.MOD_NAME, version = Constants.VERSION, clientSideOnly = true,
        guiFactory = "dev.iuif.sublang.forge.SubLangGuiFactory")
public class SubLangModForge {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Constants.LOG.info("SubLang mod pre-initializing (Forge 1.12.2)");
        SubLangConfig.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        Constants.LOG.info("SubLang mod initialized");
    }

    @SideOnly(Side.CLIENT)
    public static class ClientEventHandler {

        @SubscribeEvent
        public void onItemTooltip(ItemTooltipEvent event) {
            if (!SubLangConfig.isEnabled()) {
                return;
            }

            ItemStack stack = event.getItemStack();
            if (stack.isEmpty()) {
                return;
            }

            // Skip items with custom names
            if (stack.hasDisplayName()) {
                return;
            }

            // Get the bilingual name suffix
            String suffix = NameFormatter.getBilingualSuffix(stack);
            if (suffix != null && !suffix.isEmpty() && !event.getToolTip().isEmpty()) {
                // Modify the first line (item name) to include bilingual format
                String originalName = event.getToolTip().get(0);
                event.getToolTip().set(0, originalName + suffix);
            }
        }

        @SubscribeEvent
        public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
            LanguageManager.invalidateCache();
        }
    }
}
