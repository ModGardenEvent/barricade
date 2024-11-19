package net.modgarden.barricade.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.modgarden.barricade.data.BlockedDirections;

import java.util.HashMap;
import java.util.Map;

public class DirectionalBarrierBlock extends BarrierBlock {
    public static final MapCodec<DirectionalBarrierBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockedDirections.CODEC.fieldOf("directions").forGetter(DirectionalBarrierBlock::directions),
            propertiesCodec()
    ).apply(inst, DirectionalBarrierBlock::new));
    private static final Map<Direction, DirectionalBarrierBlock> DIRECTION_MAP = new HashMap<>() {
        @Override
        public DirectionalBarrierBlock put(Direction key, DirectionalBarrierBlock value) {
            if (containsKey(key))
                throw new RuntimeException("Cannot add direction '" + key.getName() + "' to map when it has already been added.");
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends Direction, ? extends DirectionalBarrierBlock> m) {
            if (m.keySet().stream().anyMatch(m::containsKey))
                throw new RuntimeException("Cannot add directions to map when one has already been added.");
            super.putAll(m);
        }
    };

    private final BlockedDirections directions;

    public DirectionalBarrierBlock(BlockedDirections directions, Properties properties) {
        super(properties);
        this.directions = directions;
        if (directions.directions().size() == 1)
            DIRECTION_MAP.put(directions.directions().stream().findFirst().get(), this);
    }

    public BlockedDirections directions() {
        return directions;
    }

    @Override
    public MapCodec<BarrierBlock> codec() {
        return CODEC.xmap(dir -> dir, barrierBlock -> (DirectionalBarrierBlock) barrierBlock);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!directions.doesNotBlock() && (directions.blocksAll() || directions.shouldBlock(pos, context)))
            return super.getCollisionShape(state, level, pos, context);
        return Shapes.empty();
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        if (!(state.getBlock() instanceof DirectionalBarrierBlock directional) || directional.directions.directions().size() != 1)
            return state;
        return DIRECTION_MAP.get(rot.rotate(directional.directions.directions().stream().findFirst().get())).defaultBlockState();
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        if (!(state.getBlock() instanceof DirectionalBarrierBlock directional) || directional.directions.directions().size() != 1)
            return state;
        return DIRECTION_MAP.get(mirror.mirror(directional.directions.directions().stream().findFirst().get())).defaultBlockState();
    }
}
