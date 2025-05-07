package net.modgarden.barricade.mixin;

import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HorizontalDirectionalBlock.class)
public interface HorizontalDirectionalBlockAccessor {
	@Invoker
	BlockState invokeRotate(BlockState state, Rotation rot);

	@Invoker
	BlockState invokeMirror(BlockState state, Mirror mirror);
}
