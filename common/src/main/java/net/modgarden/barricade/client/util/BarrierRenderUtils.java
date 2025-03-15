package net.modgarden.barricade.client.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.OperatorBakedModelAccess;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.item.AdvancedBarrierBlockItem;
import net.modgarden.barricade.mixin.client.ClientChunkCacheAccessor;
import net.modgarden.barricade.mixin.client.ClientChunkCacheStorageAccessor;
import net.modgarden.barricade.mixin.client.LevelRendererInvoker;
import net.modgarden.barricade.particle.AdvancedBarrierParticleOptions;
import net.modgarden.barricade.registry.BarricadeItems;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class BarrierRenderUtils {
    public static void refreshOperatorBlocks(ItemStack stack, ItemStack previousStack, ItemStack otherHandStack) {
        if (BarricadeClient.CONFIG.get().everythingVisible())
            return;

        if (testOtherStack(stack, otherHandStack) || testOtherStack(previousStack, otherHandStack))
            return;

        if ((stack.getItem() instanceof AdvancedBarrierBlockItem advancedBarrier && !OperatorBlockPseudoTag.Registry.get(Barricade.asResource("barriers")).blocks().contains(advancedBarrier.getBlock().builtInRegistryHolder())) || (previousStack.getItem() instanceof AdvancedBarrierBlockItem previousAdvancedBarrier && !OperatorBlockPseudoTag.Registry.get(Barricade.asResource("barriers")).blocks().contains(previousAdvancedBarrier.getBlock().builtInRegistryHolder()))) {
            Holder<Block> currentBlock = null;
            Holder<Block> previousBlock = null;
            Either<OperatorBlockPseudoTag, ResourceKey<Block>> current = null;
            Either<OperatorBlockPseudoTag, ResourceKey<Block>> previous = null;
            if (stack.getItem() instanceof BlockItem blockItem) {
                BlockState state = previousStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
                if (BarricadeClient.CONFIG.get().visibleBlocks().stream().noneMatch(either -> either.map(tag -> OperatorBlockPseudoTag.Registry.get(tag).blocks().contains(state.getBlockHolder()), key -> state.getBlockHolder().is(key))) &&
                        Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model) {
                    currentBlock = state.getBlockHolder();
                    current = model.requiredBlock();
                }
            }

            if (previousStack.getItem() instanceof BlockItem blockItem) {
                BlockState state = previousStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
                if (BarricadeClient.CONFIG.get().visibleBlocks().stream().noneMatch(either -> either.map(tag -> OperatorBlockPseudoTag.Registry.get(tag).blocks().contains(state.getBlockHolder()), key -> state.getBlockHolder().is(key))) &&
                        Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model) {
                    previousBlock = state.getBlockHolder();
                    previous = model.requiredBlock();
                }
            }

            if (stack.is(BarricadeItems.ADVANCED_BARRIER))
                current = Either.left(OperatorBlockPseudoTag.Registry.get(Barricade.asResource("barriers")));
            if (previousStack.is(BarricadeItems.ADVANCED_BARRIER))
                previous = Either.left(OperatorBlockPseudoTag.Registry.get(Barricade.asResource("barriers")));

            refreshOperatorSections(currentBlock, previousBlock, current, previous);
        } else if (
                stack.getItem() instanceof BlockItem blockItem && BarricadeClient.CONFIG.get().visibleBlocks().stream().noneMatch(either -> either.map(tag -> OperatorBlockPseudoTag.Registry.get(tag).blocks().contains(blockItem.getBlock().builtInRegistryHolder()), key -> blockItem.getBlock().defaultBlockState().is(key))) &&
                        previousStack.getItem() instanceof BlockItem previousBlockItem && BarricadeClient.CONFIG.get().visibleBlocks().stream().noneMatch(either -> either.map(tag -> OperatorBlockPseudoTag.Registry.get(tag).blocks().contains(previousBlockItem.getBlock().builtInRegistryHolder()), key -> previousBlockItem.getBlock().defaultBlockState().is(key)))
        ) {
            BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            BlockState previousState = previousStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(previousBlockItem.getBlock().defaultBlockState());
            BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
            BakedModel previousModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(previousState);
            if (!(model instanceof OperatorBakedModelAccess) && (!(previousModel instanceof OperatorBakedModelAccess)))
                return;
            if (testForOpposites(state, previousState, model, previousModel))
                refreshOperatorSections(state.getBlockHolder(), previousState.getBlockHolder(), model instanceof OperatorBakedModelAccess operatorModel ? operatorModel.requiredBlock() : null, previousModel instanceof OperatorBakedModelAccess operatorModel ? operatorModel.requiredBlock() : null);
        } else if (stack.getItem() instanceof BlockItem blockItem &&
                BarricadeClient.CONFIG.get().visibleBlocks().stream().noneMatch(either -> either.map(tag -> OperatorBlockPseudoTag.Registry.get(tag).blocks().contains(blockItem.getBlock().builtInRegistryHolder()), key -> blockItem.getBlock().defaultBlockState().is(key)))) {
            BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model)
                refreshOperatorSections(state.getBlockHolder(), null, model.requiredBlock(), null);
        } else if (previousStack.getItem() instanceof BlockItem blockItem &&
                BarricadeClient.CONFIG.get().visibleBlocks().stream().noneMatch(either -> either.map(tag -> OperatorBlockPseudoTag.Registry.get(tag).blocks().contains(blockItem.getBlock().builtInRegistryHolder()), key -> blockItem.getBlock().defaultBlockState().is(key)))) {
            BlockState state = previousStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model)
                refreshOperatorSections(null, state.getBlockHolder(), null, model.requiredBlock());
        }
    }

    private static boolean testForOpposites(BlockState state, BlockState previousState, BakedModel model, BakedModel previousModel) {
        if (!(model instanceof OperatorBakedModelAccess) && !(previousModel instanceof OperatorBakedModelAccess))
            return false;

        return !(model instanceof OperatorBakedModelAccess operatorModel) || !(previousModel instanceof OperatorBakedModelAccess previousOperatorModel) || !operatorModel.requiredBlock().equals(previousOperatorModel.requiredBlock());
    }

    private static boolean testOtherStack(ItemStack stack, ItemStack otherHandStack) {
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(otherHandStack.getItem() instanceof BlockItem otherBlockItem))
            return false;
        BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
        BlockState otherState = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(otherBlockItem.getBlock().defaultBlockState());
        if (!(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model) || !(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(otherState) instanceof OperatorBakedModelAccess otherModel))
            return false;
        return model.requiredBlock().map(tag -> tag.blocks().contains(otherBlockItem.getBlock().builtInRegistryHolder()), key -> otherBlockItem.getBlock().builtInRegistryHolder().is(key)) || otherModel.requiredBlock().map(tag -> tag.blocks().contains(blockItem.getBlock().builtInRegistryHolder()), key -> blockItem.getBlock().builtInRegistryHolder().is(key));
    }

    public static void refreshAllOperatorBlocks() {
        Set<SectionPos> operatedSectionPos = new HashSet<>();
        var chunks = ((ClientChunkCacheStorageAccessor)(Object)((ClientChunkCacheAccessor) Minecraft.getInstance().level.getChunkSource()).getStorage()).getChunks();
        for (int i = 0; i < chunks.length(); ++i) {
            LevelChunk chunk = chunks.get(i);
            if (chunk == null)
                continue;
            chunk.findBlocks(state -> Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess, (pos, state) -> {
                setBlockDirty(pos, operatedSectionPos);
            });
        }
    }

    public static void refreshOperatorSections(@Nullable Holder<Block> block, @Nullable Holder<Block> lastBlock, @Nullable Either<OperatorBlockPseudoTag, ResourceKey<Block>> current, @Nullable Either<OperatorBlockPseudoTag, ResourceKey<Block>> previous) {
        Set<SectionPos> operatedSectionPos = new HashSet<>();
        var chunks = ((ClientChunkCacheStorageAccessor)(Object)((ClientChunkCacheAccessor) Minecraft.getInstance().level.getChunkSource()).getStorage()).getChunks();
        for (int i = 0; i < chunks.length(); ++i) {
            LevelChunk chunk = chunks.get(i);
            if (chunk == null)
                continue;
            chunk.findBlocks(state -> Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof OperatorBakedModelAccess model && (block != null && current != null && model.requiredBlock().map(tag -> tag.blocks().contains(block), block::is) || lastBlock != null && previous != null && model.requiredBlock().map(tag -> tag.blocks().contains(lastBlock), lastBlock::is)), (pos, state) ->
                    setBlockDirty(pos, operatedSectionPos));
        }
    }

    private static void setBlockDirty(BlockPos pos, Set<SectionPos> operatedSectionPos) {
        var sectionPos = SectionPos.of(pos);
        if (operatedSectionPos.contains(sectionPos))
            return;
        ((LevelRendererInvoker)Minecraft.getInstance().levelRenderer).barricade$invokeSetBlockDirty(pos,true);
        operatedSectionPos.add(sectionPos);
    }

    public static void createAdvancedParticle(BlockedDirections directions, @Nullable ResourceLocation icon, Consumer<ParticleOptions> optionsConsumer, BlockPos pos) {
        if (directions.doesNotBlock())
            optionsConsumer.accept(new AdvancedBarrierParticleOptions(new BlockedDirections(EnumSet.noneOf(Direction.class)), icon, Optional.of(pos)));
        else if (directions.blocksAll() && icon == null)
            optionsConsumer.accept(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.BARRIER.defaultBlockState()));
        else {
            optionsConsumer.accept(new AdvancedBarrierParticleOptions(directions, icon, Optional.of(pos)));
        }
    }

    public static BlockedDirections relativeDirectionsComponent(BlockedDirections directions, BlockPos pos) {
        Set<Direction> directionSet = new HashSet<>();
        Direction relativeHorizontal = getRelativeHorizontalDirectionToPlayer();
        Direction relativeVertical = getRelativeVerticalDirectionToPlayer(pos);

        Direction relativeNorth;
        Direction relativeSouth;
        Direction relativeWest;
        Direction relativeEast;
        Direction relativeUp;
        Direction relativeDown;

        switch (relativeHorizontal) {
            case SOUTH -> {
                relativeSouth = Direction.NORTH;
                relativeNorth = Direction.SOUTH;
                relativeWest = Direction.EAST;
                relativeEast = Direction.WEST;
            }
            case WEST -> {
                relativeSouth = Direction.WEST;
                relativeNorth = Direction.EAST;
                relativeWest = Direction.NORTH;
                relativeEast = Direction.SOUTH;
            }
            case EAST -> {
                relativeSouth = Direction.EAST;
                relativeNorth = Direction.WEST;
                relativeWest = Direction.SOUTH;
                relativeEast = Direction.NORTH;
            }
            default -> {
                relativeSouth = Direction.SOUTH;
                relativeNorth = Direction.NORTH;
                relativeWest = Direction.WEST;
                relativeEast = Direction.EAST;
            }
        }

        switch (relativeVertical) {
            case UP -> {
                relativeSouth = Direction.UP;
                relativeNorth = Direction.DOWN;
                relativeUp = Direction.SOUTH;
                relativeDown = Direction.NORTH;
            }
            case DOWN -> {
                relativeSouth = Direction.DOWN;
                relativeNorth = Direction.UP;
                relativeUp = Direction.NORTH;
                relativeDown = Direction.SOUTH;
            }
            case null, default -> {
                relativeUp = Direction.UP;
                relativeDown = Direction.DOWN;
            }
        }

        if (directions.blocks(Direction.NORTH))
            directionSet.add(relativeNorth);
        if (directions.blocks(Direction.SOUTH))
            directionSet.add(relativeSouth);
        if (directions.blocks(Direction.WEST))
            directionSet.add(relativeWest);
        if (directions.blocks(Direction.EAST))
            directionSet.add(relativeEast);
        if (directions.blocks(Direction.UP))
            directionSet.add(relativeUp);
        if (directions.blocks(Direction.DOWN))
            directionSet.add(relativeDown);

        return BlockedDirections.of(directionSet.toArray(Direction[]::new));
    }

    private static Direction getRelativeHorizontalDirectionToPlayer() {
        Player player = Minecraft.getInstance().player;
        return player.getDirection();
    }

    private static Direction getRelativeVerticalDirectionToPlayer(BlockPos pos) {
        Player player = Minecraft.getInstance().player;

        if (player == null)
            return null;

        Vec3 direction = new Vec3(0, 1, 0);
        double dot = direction.dot(player.position().subtract(pos.getCenter()));

        if (dot >= (0.5 * (player.position().subtract(pos.getCenter()).length())))
            return Direction.UP;

        if (dot <= (-0.9 * (player.position().subtract(pos.getCenter()).length())))
            return Direction.DOWN;

        return null;
    }
}
