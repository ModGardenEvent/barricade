package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.block.entity.EntityBarrierBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EntityBarrierBlock extends BarrierBlock implements EntityBlock {
    public static final MapCodec<BarrierBlock> CODEC = simpleCodec(EntityBarrierBlock::new);

    public EntityBarrierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BarrierBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof EntityBarrierBlockEntity blockEntity) {
            if ((blockEntity.getBlockedEntities() == null || blockEntity.getBlockedEntities().canPass(context)))
                return Shapes.empty();
            if (blockEntity.getBlockedDirections() != null && blockEntity.getBlockedDirections().doesNotBlock())
                return Shapes.empty();
            if (blockEntity.getBlockedDirections() == null || blockEntity.getBlockedDirections().blocksAll())
                return super.getCollisionShape(state, level, pos, context);
            Direction direction = blockEntity.getBlockedDirections().blockingDirection(pos, context);
            if (direction == null)
                return Shapes.empty();
            return Shapes.block().getFaceShape(direction);
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EntityBarrierBlockEntity(pos, state);
    }
}
