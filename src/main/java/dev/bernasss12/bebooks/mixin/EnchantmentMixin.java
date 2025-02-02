package dev.bernasss12.bebooks.mixin;


import dev.bernasss12.bebooks.BetterEnchantedBooks;
import dev.bernasss12.bebooks.client.gui.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(Enchantment.class)
@Environment(EnvType.CLIENT)
public abstract class EnchantmentMixin {
    @Inject(at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, method = "getName(I)Lnet/minecraft/text/Text;")
    private void appendMaxEnchantmentLevel(int level, CallbackInfoReturnable<Text> info, MutableText enchantmentName) {
        if (ModConfig.doShowEnchantmentMaxLevel && (level != 1 || this.getMaxLevel() != 1) &&
            BetterEnchantedBooks.shouldShowEnchantmentMaxLevel.get()) {
            enchantmentName.append("/").append(Text.translatable("enchantment.level." + this.getMaxLevel()));
            BetterEnchantedBooks.shouldShowEnchantmentMaxLevel.set(false);
        }
    }
    
    @Shadow
    public abstract int getMaxLevel();
}
