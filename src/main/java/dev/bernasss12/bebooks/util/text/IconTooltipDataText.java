package dev.bernasss12.bebooks.util.text;


import net.minecraft.item.ItemStack;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;

import java.util.Collections;
import java.util.List;


public record IconTooltipDataText(List<ItemStack> icons) implements OrderedText, Text {
    
    @Override
    public boolean accept(CharacterVisitor visitor) {
        return false;
    }
    
    @Override
    public Style getStyle() {
        return Style.EMPTY;
    }
    
    @Override
    public TextContent getContent() {
        return TextContent.EMPTY;
    }
    
    @Override
    public List<Text> getSiblings() {
        return Collections.emptyList();
    }
    
    @Override
    public MutableText copy() {
        return Text.literal("Do not try to copy this. BetterEnchantedBooks - IconTooltipDataText");
    }
    
    @Override
    public OrderedText asOrderedText() {
        return this;
    }
}
