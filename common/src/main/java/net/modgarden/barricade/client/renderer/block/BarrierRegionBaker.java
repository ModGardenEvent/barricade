package net.modgarden.barricade.client.renderer.block;

import net.modgarden.barricade.registry.BarricadeTags;

/**
 * The {@link RegionBaker} that all barriers use.<br>
 * Only render this {@link RegionBaker} when the player is an operator
 * and holding a barrier.
 */
public interface BarrierRegionBaker extends RegionBaker {
	@Override
	default boolean shouldRender(BakedRegion.RenderContext context) {
		return context.player().getAbilities().instabuild && context.player().getMainHandItem().is(BarricadeTags.ItemTags.BARRIERS);
	}
}
