package net.modgarden.barricade.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeTags;

import java.util.concurrent.CompletableFuture;

public class BarricadeDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(BlockTagProvider::new);
    }

    private static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
        public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            getOrCreateTagBuilder(BarricadeTags.BlockTags.DIRECTION_ALLOWED_BARRIERS)
                    .add(BarricadeBlocks.DIRECTIONAL_BARRIER)
                    .add(BarricadeBlocks.ENTITY_BARRIER);

            getOrCreateTagBuilder(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                    .add(BarricadeBlocks.DIRECTIONAL_BARRIER)
                    .add(BarricadeBlocks.ENTITY_BARRIER);
            getOrCreateTagBuilder(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)
                    .add(BarricadeBlocks.DIRECTIONAL_BARRIER)
                    .add(BarricadeBlocks.ENTITY_BARRIER);
            getOrCreateTagBuilder(BlockTags.DRAGON_IMMUNE)
                    .add(BarricadeBlocks.DIRECTIONAL_BARRIER)
                    .add(BarricadeBlocks.ENTITY_BARRIER);
            getOrCreateTagBuilder(BlockTags.WITHER_IMMUNE)
                    .add(BarricadeBlocks.DIRECTIONAL_BARRIER)
                    .add(BarricadeBlocks.ENTITY_BARRIER);
        }
    }

    private static class EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

        public EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void addTags(HolderLookup.Provider wrapperLookup) {
            // TODO: This. Ideally, we get these tags in 'c' at some point.
            getOrCreateTagBuilder(BarricadeTags.EntityTags.PASSIVES)
                    .add(EntityType.ALLAY)
                    .add(EntityType.ARMADILLO)
                    .add(EntityType.AXOLOTL)
                    .add(EntityType.BAT)
                    .add(EntityType.BEE)
                    .add(EntityType.CAMEL)
                    .add(EntityType.CAT);
            getOrCreateTagBuilder(BarricadeTags.EntityTags.HOSTILES)
                    .add(EntityType.BLAZE)
                    .add(EntityType.BOGGED)
                    .add(EntityType.BREEZE)
                    .add(EntityType.CAVE_SPIDER);
        }
    }

}
