package dev.bernasss12.bebooks.client.gui.tooltip;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.List;


public record IconTooltipComponent(float scale, List<ItemStack> icons) implements TooltipComponent {
    @Override
    public int getHeight() {
        return (((icons.size() / 8) + 1) * (int) (16 / scale)) + 2;
    }
    
    @Override
    public int getWidth(TextRenderer textRenderer) {
        if (icons.size() >= 8)
            return 8 * (int) (16 / scale);
        else
            return icons.size() * (int) (16 / scale);
    }
    
    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrixStack, ItemRenderer itemRenderer, int z) {
        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);
        int scaledOffset = (int) (16 / scale);
        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.push();
        matrices.scale(scale, scale, 1.0f);
        int j = -1;
        for (int i = 0; i < icons.size(); i++) {
            if (i % 8 == 0)
                j++;
            itemRenderer.renderInGuiWithOverrides(icons.get(i), scaledX + (scaledOffset * (i - (j * 8))), scaledY + (scaledOffset * j), -1);
        }
        matrices.pop();
    }
}
