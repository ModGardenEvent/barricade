package net.modgarden.barricade.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.component.BlockedEntitiesComponent;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

public class BarricadeComponents {
    public static final DataComponentType<BlockedDirectionsComponent> BLOCKED_DIRECTIONS = DataComponentType.<BlockedDirectionsComponent>builder()
            .persistent(BlockedDirectionsComponent.CODEC)
            .networkSynchronized(BlockedDirectionsComponent.STREAM_CODEC)
            .build();
    public static final DataComponentType<BlockedEntitiesComponent> BLOCKED_ENTITIES = DataComponentType.<BlockedEntitiesComponent>builder()
            .persistent(BlockedEntitiesComponent.CODEC)
            .networkSynchronized(BlockedEntitiesComponent.STREAM_CODEC)
            .build();

    public static void registerAll(RegistrationCallback<DataComponentType<?>> callback) {
        callback.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Barricade.asResource("blocked_directions"), BLOCKED_DIRECTIONS);
        callback.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Barricade.asResource("blocked_entities"), BLOCKED_ENTITIES);
    }
}
