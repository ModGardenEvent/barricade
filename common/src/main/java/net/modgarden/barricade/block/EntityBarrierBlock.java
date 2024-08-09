package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.component.BlockedEntitiesComponent;

public class EntityBarrierBlock extends BarrierBlock {
    public static final MapCodec<EntityBarrierBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockedEntitiesComponent.CODEC.fieldOf("entities").forGetter(EntityBarrierBlock::entities),
            propertiesCodec()
    ).apply(inst, EntityBarrierBlock::new));

    public BlockedEntitiesComponent entities;

    public EntityBarrierBlock(BlockedEntitiesComponent entities, Properties properties) {
        super(properties);
        this.entities = entities;
    }

    public BlockedEntitiesComponent entities() {
        return entities;
    }

    @Override
    public MapCodec<BarrierBlock> codec() {
        return CODEC.xmap(dir -> dir, barrierBlock -> (EntityBarrierBlock) barrierBlock);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (entities.canPass(context))
            return Shapes.empty();
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }
}
