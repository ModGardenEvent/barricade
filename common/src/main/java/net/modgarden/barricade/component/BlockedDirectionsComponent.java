package net.modgarden.barricade.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record BlockedDirectionsComponent(Set<Direction> directions) {
    public static final Codec<BlockedDirectionsComponent> CODEC = StringRepresentable.fromEnum(Direction::values).listOf().flatXmap(directions -> DataResult.success(new BlockedDirectionsComponent(Set.copyOf(directions))), component -> DataResult.success(List.copyOf(component.directions)));
    public static final StreamCodec<ByteBuf, BlockedDirectionsComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static BlockedDirectionsComponent of(Direction... directions) {
        return new BlockedDirectionsComponent(Arrays.stream(directions).collect(Collectors.toUnmodifiableSet()));
    }

    public Direction blockingDirection(BlockPos pos, CollisionContext context) {
        if (!(context instanceof EntityCollisionContext entityContext) || entityContext.getEntity() == null)
            return null;
        Entity entity = entityContext.getEntity();
        for (Direction direction : directions) {
            Direction.Axis axis = direction.getAxis();

            // Prevents the entity from colliding horizontally with vertical blocks.
            double verticalDiff = Mth.abs((float) (entity.getY() + entity.getDeltaMovement().y - (pos.getY() + 0.6)));
            if (axis.isVertical() && (direction == Direction.DOWN && verticalDiff < entity.getBoundingBox().getYsize() + 0.2 || (direction == Direction.UP && verticalDiff > Math.max(0.4, entity.getBoundingBox().getYsize() / 3))))
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
    public boolean equals(Object other) {
        if (!(other instanceof BlockedDirectionsComponent component))
            return false;
        return component.directions.equals(directions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(directions);
    }
}
