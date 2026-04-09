package net.modgarden.barricade.mixin.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.client.renderer.block.BakedRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
	@Shadow
	public abstract boolean isClientSide();

	@Shadow
	public abstract BlockState getBlockState(BlockPos pos);

	private LevelMixin() {}

	@Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getChunkAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunk;"))
	private void onPlace(
			BlockPos pos,
			BlockState blockState,
			int updateFlags,
			int updateLimit,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (
				(BarricadeMod.isOperatorModel(blockState) || BarricadeMod.isOperatorModel(this.getBlockState(pos)))
				&& this.isClientSide()
		) {
			BakedRegion.putRegionAndNeighbors(BakedRegion.BakedRegionPos.fromBlockPos(pos));
		}
	}
}
