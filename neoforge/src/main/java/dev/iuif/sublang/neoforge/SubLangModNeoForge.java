package dev.iuif.sublang.neoforge;

import dev.iuif.sublang.Constants;
import dev.iuif.sublang.SubLangMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(Constants.MOD_ID)
public class SubLangModNeoForge {

    public SubLangModNeoForge(IEventBus modEventBus) {
        Constants.LOG.info("SubLang NeoForge initializing!");

        // Register client setup
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Initialize common mod on client
        event.enqueueWork(SubLangMod::init);
    }
}
