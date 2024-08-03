package net.modgarden.barricade.registry;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.component.BlockedEntitiesComponent;
import net.modgarden.barricade.item.EntityBarrierBlockItem;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

import java.util.List;

public class BarricadeItems {
    public static final BlockItem HORIZONTAL_BARRIER = new ItemNameBlockItem(BarricadeBlocks.DIRECTIONAL_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.of(Direction.NORTH, Direction.WEST, Direction.EAST, Direction.SOUTH)));
    public static final BlockItem VERTICAL_BARRIER = new ItemNameBlockItem(BarricadeBlocks.DIRECTIONAL_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.of(Direction.UP, Direction.DOWN)));
    public static final BlockItem UP_BARRIER = new ItemNameBlockItem(BarricadeBlocks.DIRECTIONAL_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.of(Direction.UP)));
    public static final BlockItem DOWN_BARRIER = new ItemNameBlockItem(BarricadeBlocks.DIRECTIONAL_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.of(Direction.DOWN)));
    public static final BlockItem NORTH_BARRIER = new ItemNameBlockItem(BarricadeBlocks.DIRECTIONAL_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.of(Direction.NORTH)));
    public static final BlockItem EAST_BARRIER = new ItemNameBlockItem(BarricadeBlocks.DIRECTIONAL_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.of(Direction.EAST)));
    public static final BlockItem WEST_BARRIER = new ItemNameBlockItem(BarricadeBlocks.DIRECTIONAL_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.of(Direction.WEST)));
    public static final BlockItem SOUTH_BARRIER = new ItemNameBlockItem(BarricadeBlocks.DIRECTIONAL_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.of(Direction.SOUTH)));

    public static final BlockItem HOSTILE_BARRIER = new EntityBarrierBlockItem(BarricadeBlocks.ENTITY_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_ENTITIES, BlockedEntitiesComponent.fromTags(Barricade.asResource("item/barricade/entity/creeper"), List.of(BarricadeTags.EntityTags.HOSTILES), false)));
    public static final BlockItem MOB_BARRIER = new EntityBarrierBlockItem(BarricadeBlocks.ENTITY_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_ENTITIES, BlockedEntitiesComponent.fromHolders(Barricade.asResource("item/barricade/entity/pig"), List.of(EntityType.PLAYER.builtInRegistryHolder()), true)));
    public static final BlockItem PASSIVE_BARRIER = new EntityBarrierBlockItem(BarricadeBlocks.ENTITY_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_ENTITIES, BlockedEntitiesComponent.fromTags(Barricade.asResource("item/barricade/entity/cow"), List.of(BarricadeTags.EntityTags.PASSIVES), false)));
    public static final BlockItem PLAYER_BARRIER = new EntityBarrierBlockItem(BarricadeBlocks.ENTITY_BARRIER, new Item.Properties().component(BarricadeComponents.BLOCKED_ENTITIES, BlockedEntitiesComponent.fromHolders(Barricade.asResource("item/barricade/entity/player"), List.of(EntityType.PLAYER.builtInRegistryHolder()), false)));

    public static void registerAll(RegistrationCallback<Item> callback) {
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("up_barrier"), UP_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("down_barrier"), DOWN_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("north_barrier"), NORTH_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("east_barrier"), EAST_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("west_barrier"), WEST_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("south_barrier"), SOUTH_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("horizontal_barrier"), HORIZONTAL_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("vertical_barrier"), VERTICAL_BARRIER);

        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("hostile_barrier"), HOSTILE_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("mob_barrier"), MOB_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("passive_barrier"), PASSIVE_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("player_barrier"), PLAYER_BARRIER);
    }
}