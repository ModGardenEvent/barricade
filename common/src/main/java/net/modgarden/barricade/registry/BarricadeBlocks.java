package net.modgarden.barricade.registry;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.AdvancedBarrierBlock;
import net.modgarden.barricade.block.EntityBarrierBlock;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.component.BlockedEntitiesComponent;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

import java.util.List;

public class BarricadeBlocks {
    public static final AdvancedBarrierBlock ADVANCED_BARRIER = new AdvancedBarrierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());

    public static final DirectionalBarrierBlock DOWN_BARRIER = new DirectionalBarrierBlock(BlockedDirectionsComponent.of(Direction.DOWN), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock UP_BARRIER = new DirectionalBarrierBlock(BlockedDirectionsComponent.of(Direction.UP), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock SOUTH_BARRIER = new DirectionalBarrierBlock(BlockedDirectionsComponent.of(Direction.SOUTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock NORTH_BARRIER = new DirectionalBarrierBlock(BlockedDirectionsComponent.of(Direction.NORTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock EAST_BARRIER = new DirectionalBarrierBlock(BlockedDirectionsComponent.of(Direction.EAST), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock WEST_BARRIER = new DirectionalBarrierBlock(BlockedDirectionsComponent.of(Direction.WEST), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock HORIZONTAL_BARRIER = new DirectionalBarrierBlock(BlockedDirectionsComponent.of(Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock VERTICAL_BARRIER = new DirectionalBarrierBlock(BlockedDirectionsComponent.of(Direction.UP, Direction.DOWN), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());

    public static final EntityBarrierBlock PLAYER_BARRIER = new EntityBarrierBlock(BlockedEntitiesComponent.fromHolders(Barricade.asResource("barricade/icon/steve"), List.of(EntityType.PLAYER.builtInRegistryHolder()), false), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final EntityBarrierBlock MOB_BARRIER = new EntityBarrierBlock(BlockedEntitiesComponent.fromHolders(Barricade.asResource("barricade/icon/pig"), List.of(EntityType.PLAYER.builtInRegistryHolder()), true), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final EntityBarrierBlock PASSIVE_BARRIER = new EntityBarrierBlock(BlockedEntitiesComponent.fromTags(Barricade.asResource("barricade/icon/cow"), List.of(BarricadeTags.EntityTags.BLOCKED_BY_PASSIVE_BARRIER), false), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final EntityBarrierBlock HOSTILE_BARRIER = new EntityBarrierBlock(BlockedEntitiesComponent.fromTags(Barricade.asResource("barricade/icon/creeper"), List.of(BarricadeTags.EntityTags.BLOCKED_BY_HOSTILE_BARRIER), false), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());

    public static void registerAll(RegistrationCallback<Block> callback) {
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("advanced_barrier"), ADVANCED_BARRIER);

        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("down_barrier"), DOWN_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("up_barrier"), UP_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("south_barrier"), SOUTH_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("north_barrier"), NORTH_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("east_barrier"), EAST_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("west_barrier"), WEST_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("horizontal_barrier"), HORIZONTAL_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("vertical_barrier"), VERTICAL_BARRIER);

        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("player_barrier"), PLAYER_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("mob_barrier"), MOB_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("passive_barrier"), PASSIVE_BARRIER);
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("hostile_barrier"), HOSTILE_BARRIER);
    }
}
