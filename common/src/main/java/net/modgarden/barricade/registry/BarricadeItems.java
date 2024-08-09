package net.modgarden.barricade.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.item.AdvancedBarrierBlockItem;
import net.modgarden.barricade.item.EntityBarrierBlockItem;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

public class BarricadeItems {
    public static final BlockItem ADVANCED_BARRIER = new AdvancedBarrierBlockItem(BarricadeBlocks.ADVANCED_BARRIER, new Item.Properties().rarity(Rarity.EPIC));

    public static final BlockItem HORIZONTAL_BARRIER = new BlockItem(BarricadeBlocks.HORIZONTAL_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem VERTICAL_BARRIER = new BlockItem(BarricadeBlocks.VERTICAL_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem DOWN_BARRIER = new BlockItem(BarricadeBlocks.DOWN_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem UP_BARRIER = new BlockItem(BarricadeBlocks.UP_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem SOUTH_BARRIER = new BlockItem(BarricadeBlocks.SOUTH_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem NORTH_BARRIER = new BlockItem(BarricadeBlocks.NORTH_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem EAST_BARRIER = new BlockItem(BarricadeBlocks.EAST_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem WEST_BARRIER = new BlockItem(BarricadeBlocks.WEST_BARRIER, new Item.Properties().rarity(Rarity.EPIC));

    public static final BlockItem PLAYER_BARRIER = new EntityBarrierBlockItem(BarricadeBlocks.PLAYER_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem MOB_BARRIER = new EntityBarrierBlockItem(BarricadeBlocks.MOB_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem PASSIVE_BARRIER = new EntityBarrierBlockItem(BarricadeBlocks.PASSIVE_BARRIER, new Item.Properties().rarity(Rarity.EPIC));
    public static final BlockItem HOSTILE_BARRIER = new EntityBarrierBlockItem(BarricadeBlocks.HOSTILE_BARRIER, new Item.Properties().rarity(Rarity.EPIC));

    public static void registerAll(RegistrationCallback<Item> callback) {
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("advanced_barrier"), ADVANCED_BARRIER);

        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("down_barrier"), DOWN_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("up_barrier"), UP_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("south_barrier"), SOUTH_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("north_barrier"), NORTH_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("east_barrier"), EAST_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("west_barrier"), WEST_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("horizontal_barrier"), HORIZONTAL_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("vertical_barrier"), VERTICAL_BARRIER);

        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("player_barrier"), PLAYER_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("mob_barrier"), MOB_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("passive_barrier"), PASSIVE_BARRIER);
        callback.register(BuiltInRegistries.ITEM, Barricade.asResource("hostile_barrier"), HOSTILE_BARRIER);
    }
}