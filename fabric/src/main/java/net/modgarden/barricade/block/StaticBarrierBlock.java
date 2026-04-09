package net.modgarden.barricade.block;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.BarrierBlock;
import net.modgarden.barricade.data.BlockedDirections;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class StaticBarrierBlock extends BarrierBlock {
	public static final Map<Identifier, StaticBarrierBlock> BARRIERS = new HashMap<>();
	private final Identifier identifier;

	protected StaticBarrierBlock(Properties properties,
			Identifier identifier
	) {
		super(properties);
		this.identifier = identifier;
	}

	public abstract BlockedDirections getBlockedDirections();

	public abstract @Nullable Identifier getIcon();

	public Identifier getIdentifier() {
		return identifier;
	}
}
