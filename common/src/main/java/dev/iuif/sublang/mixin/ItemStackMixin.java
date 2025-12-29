package dev.iuif.sublang.mixin;

import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.core.NameFormatter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /**
     * Inject at the return of getHoverName to append source language translation
     */
    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void sublang$modifyHoverName(CallbackInfoReturnable<Component> cir) {
        // Quick exit if disabled
        if (!SubLangConfig.isEnabled()) {
            return;
        }

        ItemStack self = (ItemStack) (Object) this;

        // Skip items with custom names (renamed in anvil)
        if (self.has(DataComponents.CUSTOM_NAME)) {
            return;
        }

        Component original = cir.getReturnValue();
        Component modified = NameFormatter.formatItemName(self, original);

        // Only set if actually modified
        if (modified != original) {
            cir.setReturnValue(modified);
        }
    }
}
