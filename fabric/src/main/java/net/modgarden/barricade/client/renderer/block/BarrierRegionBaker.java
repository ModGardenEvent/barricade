package net.modgarden.barricade.client.renderer.block;

import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.registry.BarricadeTags;

/**
 * The {@link RegionBaker} that all barriers use.<br>
 * Only render this {@link RegionBaker} when the player is an operator
 * and holding a barrier.
 */
public interface BarrierRegionBaker extends RegionBaker {
	@Override
	default void extractState(BakedRegion.ExtractContext context) {
		boolean shouldRender = context.player().getAbilities().instabuild && context.player().getMainHandItem().is(BarricadeTags.ItemTags.BARRIERS);
		shouldRender = shouldRender || BarricadeClient.CONFIG.get().everythingVisible();
		boolean fadeBarriers = BarricadeClient.CONFIG.get().barrierFadeTime() > 0.0f;
		if (fadeBarriers && OpacityState.shouldRender && !shouldRender) {
			OpacityState.isFading = true;
			if (!OpacityState.isChanging) {
				OpacityState.fadeTime = BarricadeClient.CONFIG.get().barrierFadeTimeTicks();
			}
			OpacityState.isChanging = true;
		} else if (fadeBarriers && !OpacityState.shouldRender && shouldRender) {
			OpacityState.isFading = false;
			if (!OpacityState.isChanging) {
				OpacityState.fadeTime = 0.0f;
			}
			OpacityState.isChanging = true;
		}
		OpacityState.shouldRender = shouldRender;
		this.getRenderState().shouldRender = shouldRender || OpacityState.isChanging;
		this.getRenderState().opacity = this.getOpacity(context);
		this.getRenderState().levelRenderState = context.levelRenderState();
	}

	default float getOpacity(BakedRegion.ExtractContext context) {
		if (OpacityState.isFading && OpacityState.fadeTime >= 0.0f) {
			OpacityState.fadeTime -= context.deltaTick();
			return OpacityState.fadeTime / BarricadeClient.CONFIG.get().barrierFadeTimeTicks();
		} else if (!OpacityState.isFading && OpacityState.fadeTime <= BarricadeClient.CONFIG.get().barrierFadeTimeTicks()) {
			OpacityState.fadeTime += context.deltaTick();
			return OpacityState.fadeTime / BarricadeClient.CONFIG.get().barrierFadeTimeTicks();
		} else {
			OpacityState.isChanging = false;
			return OpacityState.shouldRender ? 1.0f : 0.0f;
		}
	}

	class OpacityState extends RegionBaker.RenderState {
		private static float fadeTime = 0.0f;
		private static boolean isFading = false;
		private static boolean isChanging = false;
		private static boolean shouldRender = false;
	}
}
