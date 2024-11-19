package net.modgarden.barricade.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.registry.internal.RegistrationCallback;

public class BarricadeComponents {
    public static final DataComponentType<Holder<AdvancedBarrier>> ADVANCED_BARRIER = DataComponentType.<Holder<AdvancedBarrier>>builder()
            .persistent(AdvancedBarrier.CODEC)
            .networkSynchronized(AdvancedBarrier.STREAM_CODEC)
            .build();
    public static final DataComponentType<BlockedDirections> BLOCKED_DIRECTIONS = DataComponentType.<BlockedDirections>builder()
            .persistent(BlockedDirections.CODEC)
            .networkSynchronized(BlockedDirections.STREAM_CODEC)
            .build();

    public static void registerAll(RegistrationCallback<DataComponentType<?>> callback) {
        callback.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Barricade.asResource("advanced_barrier"), ADVANCED_BARRIER);
        callback.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Barricade.asResource("blocked_directions"), BLOCKED_DIRECTIONS);
    }
}
