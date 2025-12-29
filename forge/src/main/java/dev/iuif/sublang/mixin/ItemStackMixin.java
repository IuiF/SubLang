package dev.iuif.sublang.mixin;

import dev.iuif.sublang.config.SubLangConfig;
import dev.iuif.sublang.core.NameFormatter;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean hasCustomHoverName();

    /**
     * Inject at the return of getDisplayName to append source language translation
     */
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void sublang$modifyDisplayName(CallbackInfoReturnable<ITextComponent> cir) {
        // Quick exit if disabled
        if (!SubLangConfig.isEnabled()) {
            return;
        }

        ItemStack self = (ItemStack) (Object) this;

        // Skip items with custom names (renamed in anvil)
        if (hasCustomHoverName()) {
            return;
        }

        ITextComponent original = cir.getReturnValue();
        ITextComponent modified = NameFormatter.formatItemName(self, original);

        // Only set if actually modified
        if (modified != original) {
            cir.setReturnValue(modified);
        }
    }
}
