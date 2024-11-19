package net.modgarden.barricade.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.registry.BarricadeComponents;

public class AdvancedBarrierBlockItem extends EntityCheckBarrierBlockItem {
    public AdvancedBarrierBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(BarricadeComponents.ADVANCED_BARRIER) && stack.get(BarricadeComponents.ADVANCED_BARRIER).value().name().isPresent())
            return stack.get(BarricadeComponents.ADVANCED_BARRIER).value().name().get();
        return super.getName(stack);
    }
}
