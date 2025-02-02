package dev.bernasss12.bebooks;

import dev.bernasss12.bebooks.client.gui.ModConfig;
import dev.bernasss12.bebooks.client.gui.ModConfig.EnchantmentData;
import dev.bernasss12.bebooks.util.NBTUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import static dev.bernasss12.bebooks.client.gui.ModConstants.DEFAULT_BOOK_STRIP_COLOR;

@Environment(EnvType.CLIENT)
public class BetterEnchantedBooks implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("BEBooks");
    //public static final ModConfig CONFIG =

    private static final Map<ItemStack, Integer> cachedColors = new WeakHashMap<>();

    public static final ThreadLocal<ItemStack> enchantedItemStack = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
    public static final ThreadLocal<Boolean> shouldShowEnchantmentMaxLevel = ThreadLocal.withInitial(() -> false);

    private static final Map<Enchantment, List<ItemStack>> cachedApplicableEnchantments = new WeakHashMap<>();

    @Override
    public void onInitializeClient() {

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? getColorFromEnchantmentList(stack) : -1, Items.ENCHANTED_BOOK);
    }

    /**
     * This method gets called when the Title screen gets loaded, by this point all
     * enchantment registry should be done.
     */
    public static void onTitleScreenLoaded(){
        if (!ModConfig.configsFirstLoaded) {
            //TooltipDrawerHelper.populateEnchantmentIconList();
            ModConfig.loadAndPopulateConfig();
            ModConfig.saveConfig();
        }
    }

    public static int getColorFromEnchantmentList(ItemStack stack) {
        if (!ModConfig.doColorBooks) return DEFAULT_BOOK_STRIP_COLOR;

        Integer cached = cachedColors.get(stack);
        if (cached != null) return cached;

        EnchantmentData data = ModConfig.enchantmentDataMap.get(NBTUtils.getPriorityEnchantmentId(EnchantedBookItem.getEnchantmentNbt(stack), ModConfig.colorPrioritySetting));
        if (data != null) {
            cachedColors.put(stack, data.color);
            return data.color;
        }

        cachedColors.put(stack, DEFAULT_BOOK_STRIP_COLOR);
        return DEFAULT_BOOK_STRIP_COLOR;
    }

    private static List<ItemStack> computeApplicableItems(Enchantment enchantment){
        return ModConfig.checkedItemsList.stream().filter(enchantment::isAcceptableItem).collect(Collectors.toList());
    }

    public static List<ItemStack> getApplicableItems(Enchantment enchantment){
        return cachedApplicableEnchantments.computeIfAbsent(
                enchantment,
                BetterEnchantedBooks::computeApplicableItems
        );
    }

    public static void clearCachedColors() {
        cachedColors.clear();
    }
}
