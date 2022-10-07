package dev.bernasss12.bebooks.util;


import dev.bernasss12.bebooks.client.gui.ModConfig;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;


public class EnchantmentCompound {
    @NotNull
    private final NbtCompound compound;
    
    private final Enchantment enchantment;
    
    private String id = null;
    
    private String translatedName = null;
    
    private int index = -1;
    
    private boolean cursed = false;
    
    private final int level;
    
    public EnchantmentCompound(@NotNull NbtElement tag) {
        if (tag.getType() != 10) {
            throw new AssertionError("tag is not a CompoundTag");
        }
        
        this.compound = (NbtCompound) tag;
        
        Identifier identifier = EnchantmentHelper.getIdFromNbt(compound);
        this.enchantment = Registry.ENCHANTMENT.get(identifier);
        
        this.level = EnchantmentHelper.getLevelFromNbt(compound);
        
        // Items can have non-registered enchantment tags on them
        if (identifier == null || enchantment == null) {
            // dummy comparison values
            this.translatedName = "";
            this.index = 0;
            return;
        }
        
        this.cursed = this.enchantment.isCursed();
        this.id = identifier.toString(); // normalize e.g. "power" to "minecraft:power"
    }
    
    @NotNull
    public NbtCompound asCompoundTag() {
        return compound;
    }
    
    public boolean isRegistered() {
        return enchantment != null;
    }
    
    public boolean isCursed() {
        return cursed;
    }
    
    @NotNull
    public String getId() {
        return id;
    }
    
    @NotNull
    public String getTranslatedName() {
        if (this.translatedName == null) {
            lazyInit();
        }
        
        return translatedName;
    }
    
    public int getIndex() {
        if (index == -1) {
            lazyInit();
        }
        
        return index;
    }
    
    public int getLevel() {
        return level;
    }
    
    private void lazyInit() {
        ModConfig.EnchantmentData enchantmentData = ModConfig.enchantmentDataMap.get(this.id);
        if (enchantmentData != null) {
            this.translatedName = enchantmentData.getTranslatedName();
            this.index = enchantmentData.orderIndex;
        } else {
            this.translatedName = I18n.translate(this.enchantment.getTranslationKey());
            this.index = ModConfig.enchantmentDataMap.size();
        }
    }
    
    @Override
    public String toString() {
        return "id:\"" + id + "\",index:" + index + (cursed ? ",curse" : "");
    }
}
