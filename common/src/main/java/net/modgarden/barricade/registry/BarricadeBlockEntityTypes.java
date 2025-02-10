package net.modgarden.barricade.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;

public class BarricadeBlockEntityTypes {
    public static final BlockEntityType<AdvancedBarrierBlockEntity> ADVANCED_BARRIER = BlockEntityType.Builder.of(AdvancedBarrierBlockEntity::new, BarricadeBlocks.ADVANCED_BARRIER).build(null);

    public static void registerAll() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Barricade.asResource("advanced_barrier"), ADVANCED_BARRIER);
    }
}