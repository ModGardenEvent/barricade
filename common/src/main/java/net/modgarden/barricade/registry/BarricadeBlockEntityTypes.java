package net.modgarden.barricade.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.entity.DirectionalBarrierBlockEntity;
import net.modgarden.barricade.block.entity.EntityBarrierBlockEntity;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

public class BarricadeBlockEntityTypes {
    public static final BlockEntityType<DirectionalBarrierBlockEntity> DIRECTIONAL_BARRIER = BlockEntityType.Builder.of(DirectionalBarrierBlockEntity::new, BarricadeBlocks.DIRECTIONAL_BARRIER).build(null);
    public static final BlockEntityType<EntityBarrierBlockEntity> ENTITY_BARRIER = BlockEntityType.Builder.of(EntityBarrierBlockEntity::new, BarricadeBlocks.ENTITY_BARRIER).build(null);

    public static void registerAll(RegistrationCallback<BlockEntityType<?>> callback) {
        callback.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Barricade.asResource("directional_barrier"), DIRECTIONAL_BARRIER);
        callback.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Barricade.asResource("entity_barrier"), ENTITY_BARRIER);
    }
}