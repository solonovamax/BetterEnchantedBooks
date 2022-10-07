package dev.bernasss12.bebooks.util;


import dev.bernasss12.bebooks.client.gui.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtList;

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
                comparator = comparator.thenComparing((enchant) -> enchant.getLevel() * -1).thenComparing(
                        EnchantmentCompound::getTranslatedName);
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
}
