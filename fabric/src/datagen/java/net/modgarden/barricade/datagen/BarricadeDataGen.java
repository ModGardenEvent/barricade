package net.modgarden.barricade.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BarricadeDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        BlockTagProvider blockTagProvider = pack.addProvider(BlockTagProvider::new);
        pack.addProvider(EntityTypeTagProvider::new);
        pack.addProvider((output, registries) -> new ItemTagProvider(output, registries, blockTagProvider));
    }

    private static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
        public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            getOrCreateTagBuilder(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS)
                    .add(
                            BarricadeBlocks.ADVANCED_BARRIER,
                            BarricadeBlocks.DOWN_BARRIER,
                            BarricadeBlocks.UP_BARRIER,
                            BarricadeBlocks.NORTH_BARRIER,
                            BarricadeBlocks.SOUTH_BARRIER,
                            BarricadeBlocks.EAST_BARRIER,
                            BarricadeBlocks.WEST_BARRIER,
                            BarricadeBlocks.HORIZONTAL_BARRIER,
                            BarricadeBlocks.VERTICAL_BARRIER
                    );
            getOrCreateTagBuilder(BarricadeTags.BlockTags.ENTITY_BARRIERS)
                    .add(
                            BarricadeBlocks.ADVANCED_BARRIER,
                            BarricadeBlocks.PLAYER_BARRIER,
                            BarricadeBlocks.MOB_BARRIER,
                            BarricadeBlocks.PASSIVE_BARRIER,
                            BarricadeBlocks.HOSTILE_BARRIER
                    );
            getOrCreateTagBuilder(BarricadeTags.BlockTags.PREDICATE_BARRIERS)
                    .forceAddTag(BarricadeTags.BlockTags.ENTITY_BARRIERS)
                    .add(BarricadeBlocks.CREATIVE_ONLY_BARRIER);
            getOrCreateTagBuilder(BarricadeTags.BlockTags.BARRIERS)
                    .add(Blocks.BARRIER)
                    .forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS)
                    .forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
            getOrCreateTagBuilder(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                    .forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS)
                    .forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
            getOrCreateTagBuilder(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)
                    .forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS)
                    .forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
            getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE)
                    .forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS)
                    .forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
            getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE)
                    .forceAddTag(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS)
                    .forceAddTag(BarricadeTags.BlockTags.PREDICATE_BARRIERS);
        }
    }

    private static class EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

        public EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            getOrCreateTagBuilder(BarricadeTags.EntityTags.BLOCKED_BY_PASSIVE_BARRIER)
                    .add(
                            EntityType.ALLAY,
                            EntityType.ARMADILLO,
                            EntityType.AXOLOTL,
                            EntityType.BAT,
                            EntityType.BEE,
                            EntityType.CAMEL,
                            EntityType.CAT,
                            EntityType.CHICKEN,
                            EntityType.COD,
                            EntityType.COW,
                            EntityType.DOLPHIN,
                            EntityType.DONKEY,
                            EntityType.FOX,
                            EntityType.FROG,
                            EntityType.GLOW_SQUID,
                            EntityType.GOAT,
                            EntityType.HORSE,
                            EntityType.IRON_GOLEM,
                            EntityType.LLAMA,
                            EntityType.MOOSHROOM,
                            EntityType.MULE,
                            EntityType.OCELOT,
                            EntityType.PANDA,
                            EntityType.PARROT,
                            EntityType.PIG,
                            EntityType.POLAR_BEAR,
                            EntityType.PUFFERFISH,
                            EntityType.RABBIT,
                            EntityType.SALMON,
                            EntityType.SHEEP,
                            EntityType.SKELETON_HORSE,
                            EntityType.SNIFFER,
                            EntityType.SNOW_GOLEM,
                            EntityType.SQUID,
                            EntityType.STRIDER,
                            EntityType.TADPOLE,
                            EntityType.TRADER_LLAMA,
                            EntityType.TROPICAL_FISH,
                            EntityType.TURTLE,
                            EntityType.VILLAGER,
                            EntityType.WANDERING_TRADER,
                            EntityType.WOLF,
                            EntityType.ZOMBIE_HORSE
                    );
            getOrCreateTagBuilder(BarricadeTags.EntityTags.BLOCKED_BY_HOSTILE_BARRIER)
                    .add(
                            EntityType.BLAZE,
                            EntityType.BOGGED,
                            EntityType.BREEZE,
                            EntityType.CAVE_SPIDER,
                            EntityType.CREEPER,
                            EntityType.DROWNED,
                            EntityType.ELDER_GUARDIAN,
                            EntityType.ENDER_DRAGON,
                            EntityType.ENDERMAN,
                            EntityType.ENDERMITE,
                            EntityType.GHAST,
                            EntityType.GIANT,
                            EntityType.GUARDIAN,
                            EntityType.HOGLIN,
                            EntityType.HUSK,
                            EntityType.MAGMA_CUBE,
                            EntityType.PHANTOM,
                            EntityType.PIGLIN,
                            EntityType.PIGLIN_BRUTE,
                            EntityType.SHULKER,
                            EntityType.SKELETON,
                            EntityType.SLIME,
                            EntityType.SPIDER,
                            EntityType.STRAY,
                            EntityType.VEX,
                            EntityType.WARDEN,
                            EntityType.WITCH,
                            EntityType.WITHER,
                            EntityType.WITHER_SKELETON,
                            EntityType.ZOGLIN,
                            EntityType.ZOMBIE,
                            EntityType.ZOMBIE_VILLAGER
                    ).forceAddTag(EntityTypeTags.RAIDERS);
        }
    }

    private static class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
        public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
            super(output, completableFuture, blockTagProvider);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            getOrCreateTagBuilder(BarricadeTags.ItemTags.BARRIERS)
                    .add(Items.BARRIER);
            copy(BarricadeTags.BlockTags.DIRECTIONAL_BARRIERS, BarricadeTags.ItemTags.BARRIERS);
            copy(BarricadeTags.BlockTags.ENTITY_BARRIERS, BarricadeTags.ItemTags.BARRIERS);
        }
    }
}
