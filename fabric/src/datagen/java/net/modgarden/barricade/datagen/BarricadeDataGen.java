package net.modgarden.barricade.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeTags;

import java.util.concurrent.CompletableFuture;

public class BarricadeDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(BlockTagProvider::new);
        pack.addProvider(EntityTypeTagProvider::new);
    }

    private static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
        public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            getOrCreateTagBuilder(BarricadeTags.BlockTags.DIRECTION_ALLOWED_BARRIERS)
                    .add(
                        BarricadeBlocks.DIRECTIONAL_BARRIER,
                        BarricadeBlocks.ENTITY_BARRIER
                    );
            getOrCreateTagBuilder(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                    .add(
                            BarricadeBlocks.DIRECTIONAL_BARRIER,
                            BarricadeBlocks.ENTITY_BARRIER
                    );
            getOrCreateTagBuilder(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)
                    .add(
                        BarricadeBlocks.DIRECTIONAL_BARRIER,
                        BarricadeBlocks.ENTITY_BARRIER
                    );
            getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE)
                    .add(
                        BarricadeBlocks.DIRECTIONAL_BARRIER,
                        BarricadeBlocks.ENTITY_BARRIER
                    );
            getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE)

                    .add(
                            BarricadeBlocks.DIRECTIONAL_BARRIER,
                            BarricadeBlocks.ENTITY_BARRIER
                    );
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

}
