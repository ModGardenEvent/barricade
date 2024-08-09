package net.modgarden.barricade.client.util;

import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.component.BlockedEntitiesComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record AdvancedBarrierComponents(@Nullable BlockedEntitiesComponent blockedEntities, @Nullable BlockedDirectionsComponent blockedDirections) {
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AdvancedBarrierComponents components))
            return false;
        if (components.blockedEntities != null && blockedEntities == null)
            return false;
        if (components.blockedDirections == null && blockedDirections != null)
            return false;
        return (components.blockedDirections == null || components.blockedDirections.equals(blockedDirections)) && (components.blockedEntities == null || components.blockedEntities.equals(blockedEntities));
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockedEntities, blockedDirections);
    }
}