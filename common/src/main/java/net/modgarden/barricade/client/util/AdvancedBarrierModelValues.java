package net.modgarden.barricade.client.util;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.modgarden.barricade.data.BlockedDirectionsComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record AdvancedBarrierModelValues(BlockedDirectionsComponent directions, @Nullable ResourceLocation icon) {
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
        if (!(obj instanceof AdvancedBarrierModelValues values))
            return false;
        return values.directions.equals(directions) && (values.icon == null && icon == null || values.icon != null && values.icon.equals(icon) || icon != null && icon.equals(values.icon));
    }

    @Override
    public int hashCode() {
        return Objects.hash(directions, icon);
    }
}