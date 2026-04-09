package net.modgarden.barricade.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import java.util.Set;

public record BarricadeClientConfig(boolean everythingVisible,
                                    Set<Either<Identifier, ResourceKey<Block>>> visibleBlocks,
                                    float barrierFadeTime) {
	public static final BarricadeClientConfig DEFAULT = new BarricadeClientConfig(false, Set.of(), 2.0f);

	public float barrierFadeTimeTicks() {
		return barrierFadeTime * 20.0f;
	}
}
