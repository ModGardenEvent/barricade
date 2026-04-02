package net.modgarden.barricade.block;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.BarrierBlock;
import net.modgarden.barricade.data.BlockedDirections;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class StaticBarrierBlock extends BarrierBlock {
	public static final Map<Identifier, StaticBarrierBlock> BARRIERS = new HashMap<>();

	protected StaticBarrierBlock(Properties properties) {
		super(properties);
	}

	public abstract BlockedDirections getBlockedDirections();

	public abstract @Nullable Identifier getIcon();
}
