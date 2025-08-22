package net.modgarden.barricade.client.renderer.block;

import java.lang.ref.WeakReference;

public class CleanerState implements Runnable {
	private final WeakReference<RegionBaker> baker;

	public CleanerState(WeakReference<RegionBaker> baker) {
		this.baker = baker;
	}

	@Override
	public void run() {
		BakedRegion.removeRegionBaker(this.baker);
	}
}
