package dev.iuif.sublang.fabric;

import dev.iuif.sublang.Constants;
import dev.iuif.sublang.SubLangMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class SubLangModFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Constants.LOG.info("SubLang Fabric initializing!");

        // Initialize common mod
        SubLangMod.init();

        // Register resource reload listener
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return new ResourceLocation(Constants.MOD_ID, "resource_reload");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager resourceManager) {
                        SubLangMod.onResourcesReload();
                    }
                }
        );
    }
}
