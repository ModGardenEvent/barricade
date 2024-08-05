package net.modgarden.barricade.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeTags;
import net.modgarden.barricade.util.CodecUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record BlockedDirectionsComponent(Object2BooleanOpenHashMap<Direction> directionMap) {
    public static final BlockedDirectionsComponent EMPTY = new BlockedDirectionsComponent(new Object2BooleanOpenHashMap<>());

    public static final Codec<BlockedDirectionsComponent> CODEC = CodecUtil.DIRECTION_MAP_CODEC.flatComapMap(BlockedDirectionsComponent::new, blockedDirectionsComponent -> DataResult.success(blockedDirectionsComponent.directionMap));
    public static final StreamCodec<ByteBuf, BlockedDirectionsComponent> STREAM_CODEC = CodecUtil.DIRECTION_MAP_STREAM_CODEC.map(BlockedDirectionsComponent::new, BlockedDirectionsComponent::directionMap);

    public static BlockedDirectionsComponent of(Direction... directions) {
        return new BlockedDirectionsComponent(Arrays.stream(directions).collect(Object2BooleanOpenHashMap::new, (map, dir) -> map.put(dir, true), Object2BooleanOpenHashMap::putAll));
    }

    public static BlockedDirectionsComponent fromBlockState(BlockState state) {
        if (!state.is(BarricadeTags.BlockTags.DIRECTION_ALLOWED_BARRIERS))
            return EMPTY;
        Map<Direction, Boolean> map = new HashMap<>();
        if (state.getValue(DirectionalBarrierBlock.DOWN))
            map.put(Direction.DOWN, true);
        if (state.getValue(DirectionalBarrierBlock.UP))
            map.put(Direction.UP, true);
        if (state.getValue(DirectionalBarrierBlock.NORTH))
            map.put(Direction.NORTH, true);
        if (state.getValue(DirectionalBarrierBlock.EAST))
            map.put(Direction.EAST, true);
        if (state.getValue(DirectionalBarrierBlock.SOUTH))
            map.put(Direction.SOUTH, true);
        if (state.getValue(DirectionalBarrierBlock.WEST))
            map.put(Direction.WEST, true);
        return new BlockedDirectionsComponent(new Object2BooleanOpenHashMap<>(map));
    }

    public boolean matches(BlockState blockState) {
        return blockState.is(BarricadeBlocks.DIRECTIONAL_BARRIER) &&
                blocks(Direction.DOWN) == blockState.getValue(DirectionalBarrierBlock.DOWN) &&
                blocks(Direction.UP) == blockState.getValue(DirectionalBarrierBlock.UP) &&
                blocks(Direction.NORTH) == blockState.getValue(DirectionalBarrierBlock.NORTH) &&
                blocks(Direction.EAST) == blockState.getValue(DirectionalBarrierBlock.EAST) &&
                blocks(Direction.SOUTH) == blockState.getValue(DirectionalBarrierBlock.SOUTH) &&
                blocks(Direction.WEST) == blockState.getValue(DirectionalBarrierBlock.WEST);
    }

    public Direction blockingDirection(BlockPos pos, CollisionContext context) {
        if (!(context instanceof EntityCollisionContext entityContext) || entityContext.getEntity() == null)
            return null;
        Entity entity = entityContext.getEntity();
        for (Map.Entry<Direction, Boolean> entry : directionMap.object2BooleanEntrySet()) {
            if (!entry.getValue())
                continue;
            Direction direction = entry.getKey();
            Direction.Axis axis = direction.getAxis();

            // Prevents the entity from colliding horizontally with vertical blocks.
            double verticalDiff = Mth.abs((float) (entity.getY() + entity.getDeltaMovement().y - (pos.getY() + 0.6)));
            if (axis.isVertical() &&  (direction == Direction.DOWN && verticalDiff < 2 || (direction == Direction.UP && verticalDiff > 0.6)))
                continue;

            // Prevents the entity from colliding vertically on horizontal blocks.
            double horizontalDiff = entity.position().multiply(1.0F, 0.0F, 1.0F).add(entity.getDeltaMovement().multiply(1.0F, 0.0F, 1.0F)).distanceTo(pos.getCenter().add(direction.getStepX() * -0.3, direction.getStepY() * -0.3, direction.getStepZ() * -0.3).multiply(1.0F, 0.0F, 1.0F));
            if (axis.isHorizontal() && (horizontalDiff < 0.6 || !entity.getBoundingBox().inflate(1.5, 0.5, 1.5).contains(pos.getCenter().add(direction.getStepX() * 0.3, direction.getStepY() * 0.3, direction.getStepZ() * 0.3)) || verticalDiff < 0.3 || verticalDiff > 2))
                continue;

            if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE && entity.position().get(axis) > (double) pos.get(axis) + Shapes.block().max(axis) - 1.0E-5F)
                return direction;

            if (entity.position().get(axis) < (double) pos.get(axis) + Shapes.block().min(axis) + 1.0E-5F)
                return direction;
        }
        return null;
    }

    public boolean doesNotBlock() {
        return directionMap.isEmpty();
    }

    public boolean blocksAll() {
        for (Direction dir : Direction.values())
            if (!directionMap.containsKey(dir) || !directionMap.getBoolean(dir))
                return false;
        return true;
    }

    public boolean blocks(Direction direction) {
        return directionMap.containsKey(direction) && directionMap.getBoolean(direction);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BlockedDirectionsComponent component))
            return false;
        return component.directionMap.equals(directionMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(directionMap);
    }
}
