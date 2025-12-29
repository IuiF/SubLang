package dev.iuif.sublang.forge;

import dev.iuif.sublang.Constants;
import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.core.LanguageManager;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class SubLangModForge {

    public SubLangModForge() {
        Constants.LOG.info("SubLang mod initializing (Forge)");
        SubLangConfig.load();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        Constants.LOG.info("SubLang client setup complete");
    }

    @SubscribeEvent
    public void onResourceReload(AddReloadListenerEvent event) {
        // Invalidate cache when resources reload
        LanguageManager.invalidateCache();
    }

    @SubscribeEvent
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        // Invalidate cache on logout
        LanguageManager.invalidateCache();
    }
}
