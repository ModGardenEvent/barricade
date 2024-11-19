package net.modgarden.barricade.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public record BlockedDirectionsComponent(EnumSet<Direction> directions) {
    public static final Codec<BlockedDirectionsComponent> CODEC = StringRepresentable.fromEnum(Direction::values).listOf().flatXmap(directions -> {
        if (directions.isEmpty()) {
            return DataResult.error(() -> "Must specify at least one direction.");
        }
        return DataResult.success(BlockedDirectionsComponent.of(directions.toArray(Direction[]::new)));
    }, component -> DataResult.success(List.copyOf(component.directions)));
    public static final StreamCodec<ByteBuf, BlockedDirectionsComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static BlockedDirectionsComponent of(Direction... directions) {
        return new BlockedDirectionsComponent(EnumSet.copyOf(Arrays.stream(directions).toList()));
    }

    public Direction blockingDirection(BlockPos pos, CollisionContext context) {
        if (!(context instanceof EntityCollisionContext entityContext) || entityContext.getEntity() == null)
            return null;
        Entity entity = entityContext.getEntity();
        for (Direction direction : directions) {
            if (isOnOtherSideOfFace(direction, entity.getBoundingBox(), pos))
                return direction;
        }
        return null;
    }

    public static boolean isOnOtherSideOfFace(Direction direction, AABB box, BlockPos pos) {
        double boxPoint = (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? box.min(direction.getAxis()) : box.max(direction.getAxis()));
        double blockPoint = pos.get(direction.getAxis());
        if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            blockPoint = blockPoint + 1 - 1.0E-5F;
            return boxPoint > blockPoint;
        }
        blockPoint = blockPoint + 1.0E-5F;
        return boxPoint < blockPoint;
    }

    public boolean isHorizontal() {
        return directions.stream().anyMatch(direction -> direction.getAxis().isHorizontal());
    }

    public boolean isVertical() {
        return directions.stream().anyMatch(direction -> direction.getAxis().isVertical());
    }

    public boolean doesNotBlock() {
        return directions.isEmpty();
    }

    public boolean blocksAll() {
        for (Direction dir : Direction.values())
            if (!directions.contains(dir))
                return false;
        return true;
    }

    public boolean blocks(Direction direction) {
        return directions.contains(direction);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof BlockedDirectionsComponent other))
            return false;
        return other.directions.equals(directions);
    }
}
