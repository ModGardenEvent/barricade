package net.modgarden.barricade.registry;

import house.greenhouse.silicate.api.condition.InvertedCondition;
import house.greenhouse.silicate.api.condition.builtin.EntityTypeCondition;
import house.greenhouse.silicate.api.condition.builtin.PlayerGameTypeCondition;
import house.greenhouse.silicate.api.context.param.ContextParamTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.AdvancedBarrierBlock;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

public class BarricadeBlocks {
    public static final AdvancedBarrierBlock ADVANCED_BARRIER = new AdvancedBarrierBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());

    public static final DirectionalBarrierBlock DOWN_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.DOWN), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock UP_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.UP), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock SOUTH_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.SOUTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock NORTH_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.NORTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock EAST_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.EAST), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock WEST_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.WEST), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock HORIZONTAL_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    public static final DirectionalBarrierBlock VERTICAL_BARRIER = new DirectionalBarrierBlock(BlockedDirections.of(Direction.UP, Direction.DOWN), BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER).dynamicShape());
    
    // Predicate Barriers
    public static final PredicateBarrierBlock CREATIVE_ONLY_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                .dynamicShape(),
            Barricade.asResource("barricade/icon/iron_sword"),
            new PlayerGameTypeCondition(
                    ContextParamTypes.THIS_ENTITY,
                    PlayerGameTypeCondition.SURVIVAL_LIKE
            )
    );

    public static final PredicateBarrierBlock PLAYER_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("barricade/icon/steve"),
            EntityTypeCondition.of(
                    ContextParamTypes.THIS_ENTITY,
                    EntityType.PLAYER
            )
    );
    public static final PredicateBarrierBlock MOB_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("barricade/icon/pig"),
            new InvertedCondition(
                    EntityTypeCondition.of(
                            ContextParamTypes.THIS_ENTITY,
                            EntityType.PLAYER
                    )
            )
    );
    public static final PredicateBarrierBlock PASSIVE_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("barricade/icon/parrot"),
            EntityTypeCondition.of(
                    ContextParamTypes.THIS_ENTITY,
                    BarricadeTags.EntityTags.BLOCKED_BY_PASSIVE_BARRIER
            )
    );
    public static final PredicateBarrierBlock HOSTILE_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("barricade/icon/creeper"),
            EntityTypeCondition.of(
                    ContextParamTypes.THIS_ENTITY,
                    BarricadeTags.EntityTags.BLOCKED_BY_HOSTILE_BARRIER
            )
    );
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
        
        callback.register(BuiltInRegistries.BLOCK, Barricade.asResource("creative_only_barrier"), CREATIVE_ONLY_BARRIER);
    }
}
