package net.modgarden.barricade.client.renderer.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.modgarden.barricade.block.StaticBarrierBlock;
import net.modgarden.barricade.client.model.BarricadeBlockStateModel;
import net.modgarden.barricade.data.AdvancedBarrier;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;

public final class BarrierBakery2 {
	private static final Map<Identifier, BarricadeBlockStateModel> MODELS = new HashMap<>();

	private BarrierBakery2() {
	}

	public static BlockStateModel getModel(Identifier identifier) {
		return Objects.requireNonNull(MODELS.get(identifier), "Barricade model " + identifier + "does not exist");
	}

	public static void clear() {
		MODELS.clear();
	}

	public static void bake(Map<Identifier, AdvancedBarrier> barricades) {
		for (Map.Entry<Identifier, AdvancedBarrier> entry : barricades.entrySet()) {
			Identifier icon = entry.getValue().icon().orElse(AdvancedBarrier.UNKNOWN_ICON);
			MODELS.put(entry.getKey(), new BarricadeBlockStateModel(icon, entry.getValue().directions()));
		}
	}

	public static void bakeStatic(Map<Identifier, StaticBarrierBlock> blocks) {
		for (Map.Entry<Identifier, StaticBarrierBlock> entry : blocks.entrySet()) {
			StaticBarrierBlock block = entry.getValue();
			Identifier icon = block.getIcon();

			if (icon == null) {
				icon = AdvancedBarrier.UNKNOWN_ICON;
			}

			MODELS.put(entry.getKey(), new BarricadeBlockStateModel(icon, block.getBlockedDirections()));
		}
	}
}
