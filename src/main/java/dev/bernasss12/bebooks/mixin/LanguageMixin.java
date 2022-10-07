package dev.bernasss12.bebooks.mixin;


import dev.bernasss12.bebooks.RomanNumerals;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Pattern;


@Mixin(targets = "net.minecraft.util.Language$1")
abstract class LanguageMixin {
    private static final Pattern ENCHANTMENT_KEY_PATTERN = Pattern.compile("enchantment\\.level\\.\\d+");
    
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\D");
    
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void get(String key, CallbackInfoReturnable<String> info) {
        if (ENCHANTMENT_KEY_PATTERN.matcher(key).matches()) {
            info.setReturnValue(RomanNumerals.fromDecimal(Integer.parseInt(NUMBER_PATTERN.matcher(key).replaceAll(""))));
        }
    }
}
