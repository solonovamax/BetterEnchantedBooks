package dev.bernasss12.bebooks.util;


import dev.bernasss12.bebooks.client.gui.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;


@Environment(EnvType.CLIENT)
public final class NBTUtils {
    
    public static Stream<EnchantmentCompound> sorted(NbtList listTag, ModConfig.SortingSetting mode, boolean cursesBelow) {
        Comparator<EnchantmentCompound> comparator;
        
        if (cursesBelow) {
            comparator = Comparator.comparing(EnchantmentCompound::isCursed);
        } else {
            comparator = Comparator.comparing(e -> 0); // Preserve existing order
        }
        
        switch (mode) {
            case ALPHABETICALLY:
                comparator = comparator.thenComparing(EnchantmentCompound::getTranslatedName);
                break;
            case CUSTOM:
                comparator = comparator.thenComparing(EnchantmentCompound::getIndex);
                break;
            case LEVEL:
                comparator = comparator.thenComparing((enchant) -> enchant.getLevel() * -1).thenComparing(EnchantmentCompound::getTranslatedName);
                break;
            case DISABLED:
                // Can still sort by isCursed
                break;
        }
        
        return listTag.stream().map(EnchantmentCompound::new).sorted(comparator);
    }
    
    public static NbtList toListTag(Stream<EnchantmentCompound> stream) {
        NbtList listTag = new NbtList();
        stream.forEachOrdered(tag -> listTag.add(tag.asCompoundTag()));
        return listTag;
    }
    
    public static boolean hasCurses(NbtList listTag) {
        return listTag.stream().map(EnchantmentCompound::new).anyMatch(EnchantmentCompound::isCursed);
    }
    
    public static String getPriorityEnchantmentId(NbtList listTag, ModConfig.SortingSetting mode) {
        Stream<EnchantmentCompound> candidates = sorted(listTag, mode, true)
                .filter(EnchantmentCompound::isRegistered);
        
        Optional<EnchantmentCompound> priority;
        if (ModConfig.doCurseColorOverride && hasCurses(listTag)) {
            priority = candidates.reduce((a, b) -> b); // last element
        } else {
            priority = candidates.findFirst();
        }
        
        return priority.map(EnchantmentCompound::getId).orElse("");
    }
    
    public static class EnchantmentCompound {
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
}
