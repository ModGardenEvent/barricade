package net.modgarden.barricade.registry;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.GameTypePredicate;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.AdvancedBarrierBlock;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.PredicateBarrierBlock;
import net.modgarden.barricade.data.BlockedDirectionsComponent;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

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
    
    // Predicate Barriers
    public static final PredicateBarrierBlock CREATIVE_ONLY_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("item/barricade/icon/iron_sword"),
            LootItemEntityPropertyCondition.hasProperties(
                    LootContext.EntityTarget.THIS,
                    EntityPredicate.Builder.entity()
                            .of(EntityType.PLAYER)
                            .subPredicate(
                                    PlayerPredicate.Builder.player()
                                            .setGameType(GameTypePredicate.SURVIVAL_LIKE)
                                            .build()
                            )
                            .build()
            ).build()
    );

    public static final PredicateBarrierBlock PLAYER_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("item/barricade/icon/steve"),
            InvertedLootItemCondition.invert(
                    LootItemEntityPropertyCondition.hasProperties(
                            LootContext.EntityTarget.THIS,
                            EntityPredicate.Builder.entity()
                                    .of(EntityType.PLAYER)
                                    .build()
                    )
            ).build()
    );
    public static final PredicateBarrierBlock MOB_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("item/barricade/icon/pig"),
            LootItemEntityPropertyCondition.hasProperties(
                    LootContext.EntityTarget.THIS,
                    EntityPredicate.Builder.entity()
                            .of(EntityType.PLAYER)
                            .build()
            ).build()
    );
    public static final PredicateBarrierBlock PASSIVE_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("item/barricade/icon/parrot"),
            LootItemEntityPropertyCondition.hasProperties(
                    LootContext.EntityTarget.THIS,
                    EntityPredicate.Builder.entity()
                            .of(BarricadeTags.EntityTags.BLOCKED_BY_PASSIVE_BARRIER)
                            .build()
            ).build()
    );
    public static final PredicateBarrierBlock HOSTILE_BARRIER = new PredicateBarrierBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.BARRIER)
                    .dynamicShape(),
            Barricade.asResource("item/barricade/icon/creeper"),
            LootItemEntityPropertyCondition.hasProperties(
                    LootContext.EntityTarget.THIS,
                    EntityPredicate.Builder.entity()
                            .of(BarricadeTags.EntityTags.BLOCKED_BY_HOSTILE_BARRIER)
                            .build()
            ).build()
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
