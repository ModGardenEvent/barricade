package net.modgarden.barricade.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.EntityBarrierBlock;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

public class BarricadeBlocks {
    public static final DirectionalBarrierBlock DIRECTIONAL_BARRIER = new DirectionalBarrierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final EntityBarrierBlock ENTITY_BARRIER = new EntityBarrierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());

    public static void registerAll(RegistrationCallback<Block> callback) {
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("directional_barrier"), DIRECTIONAL_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("entity_barrier"), ENTITY_BARRIER);
    }
}
