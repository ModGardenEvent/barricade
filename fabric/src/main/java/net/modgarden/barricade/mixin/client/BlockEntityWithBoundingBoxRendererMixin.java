package net.modgarden.barricade.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.modgarden.barricade.registry.BarricadeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityWithBoundingBoxRenderState;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

@Mixin(BlockEntityWithBoundingBoxRenderer.class)
public class BlockEntityWithBoundingBoxRendererMixin {
	@Unique
	private static final RenderStateDataKey<BlockState[]> BARRICADES = RenderStateDataKey.create();

	@Definition(id = "blockState", local = @Local(type = BlockState.class, name = "blockState"))
	@Expression("blockState = ?")
	@Inject(method = "extract", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
	private static void onExtractStates(
			BlockEntity blockEntity,
			BlockEntityWithBoundingBoxRenderState state,
			CallbackInfo ci,
			@Local(name = "blockState") BlockState blockState,
			@Local(name = "index") int index,
			@Local(name = "size") Vec3i size
	) {
		if (blockState.is(BarricadeTags.BlockTags.BARRIERS)) {
			BlockState[] barricades = state.getData(BARRICADES);

			if (barricades == null) {
				barricades = new BlockState[size.getX() * size.getY() * size.getZ()];
			}

			barricades[index] = blockState;
			state.setData(BARRICADES, barricades);
		}
	}

	@Definition(id = "invisibleBlockType", local = @Local(type = net.minecraft.client.renderer.blockentity.state.BlockEntityWithBoundingBoxRenderState.InvisibleBlockType.class, name = "invisibleBlockType"))
	@Expression("invisibleBlockType != null")
	@WrapOperation(method = "renderInvisibleBlocks", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean allowOtherBarriers(
			Object left,
			Object right,
			Operation<Boolean> original,
			@SuppressWarnings("LocalMayUseName") // mixin won't accept it
			@Local(argsOnly = true) BlockEntityWithBoundingBoxRenderState state,
			@Local(name = "index") int index
	) {
		BlockState[] barricades = state.getData(BARRICADES);

		if (barricades == null) {
			return original.call(left, right);
		}

		BlockState blockState = barricades[index];

		if (blockState == null) {
			return original.call(left, right);
		}

		if (blockState.is(BarricadeTags.BlockTags.BARRIERS)) {
			return true;
		}

		return original.call(left, right);
	}
}
