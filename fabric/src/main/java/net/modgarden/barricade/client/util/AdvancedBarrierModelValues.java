package net.modgarden.barricade.client.util;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.modgarden.barricade.data.BlockedDirections;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record AdvancedBarrierModelValues(BlockedDirections directions,
                                         @Nullable Identifier icon) {
	public String getVariant() {
		String variant = "";
		if (icon() != null)
			variant = icon().toString();

		if (!directions().doesNotBlock())
			variant = (variant.isEmpty() ? "" : ",") + String.join(",", directions.directions().stream().map(Direction::getName).toList());
		return variant;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AdvancedBarrierModelValues(BlockedDirections directions1, Identifier icon1)))
			return false;
		return directions1.equals(directions) && (icon1 == null && icon == null || icon1 != null && icon1.equals(icon) || icon != null && icon.equals(icon1));
	}

	@Override
	public int hashCode() {
		return Objects.hash(directions, icon);
	}
}
