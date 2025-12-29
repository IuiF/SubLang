package dev.iuif.sublang.neoforge;

import dev.iuif.sublang.Constants;
import dev.iuif.sublang.SubLangMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(Constants.MOD_ID)
public class SubLangModNeoForge {

    public SubLangModNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        Constants.LOG.info("SubLang NeoForge initializing!");

        // Register config screen
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (mc, parent) -> SubLangConfigScreen.create(parent));

        // Register client setup
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Initialize common mod on client
        event.enqueueWork(SubLangMod::init);
    }
}
