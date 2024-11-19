package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import org.jetbrains.annotations.Nullable;

public class AdvancedBarrierBlock extends BarrierBlock implements EntityBlock {
    public static final MapCodec<BarrierBlock> CODEC = simpleCodec(AdvancedBarrierBlock::new);

    public AdvancedBarrierBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public MapCodec<BarrierBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity && context instanceof EntityCollisionContext entityContext) {
            ServerLevel serverLevel = null;
            if (level instanceof ServerLevel)
                serverLevel = (ServerLevel) level;
            if (!(blockEntity.getData().directions().doesNotBlock() || entityContext.getEntity() != null && blockEntity.getData().test(serverLevel, entityContext.getEntity(), state, pos)) && (blockEntity.getData().directions().blocksAll() || !blockEntity.getData().directions().test(pos, context)))
                return Shapes.empty();
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (
                level.getBlockEntity(pos) instanceof AdvancedBarrierBlockEntity blockEntity &&
                context instanceof EntityCollisionContext entityContext &&
                entityContext.getEntity() instanceof Player player &&
                !player.canUseGameMasterBlocks()
        ) {
            ServerLevel serverLevel = null;
            if (level instanceof ServerLevel)
                serverLevel = (ServerLevel) level;
            if (!blockEntity.getData().test(serverLevel, entityContext.getEntity(), state, pos))
                return Shapes.empty();
        }
        return super.getShape(state, level, pos, context);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdvancedBarrierBlockEntity(pos, state);
    }
}
