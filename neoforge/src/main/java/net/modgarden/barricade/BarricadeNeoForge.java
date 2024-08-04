package net.modgarden.barricade;

import net.modgarden.barricade.platform.BarricadePlatformHelperNeoForge;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.barricade.registry.BarricadeItems;
import net.modgarden.barricade.registry.internal.RegistrationCallback;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@Mod(Barricade.MOD_ID)
public class BarricadeNeoForge {
    public BarricadeNeoForge(IEventBus eventBus) {
        Barricade.init();
        Barricade.setHelper(new BarricadePlatformHelperNeoForge());
    }

    @EventBusSubscriber(modid = Barricade.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            register(event, BarricadeBlocks::registerAll);
            register(event, BarricadeBlockEntityTypes::registerAll);
            register(event, BarricadeComponents::registerAll);
            register(event, BarricadeItems::registerAll);
        }

        private static <T> void register(RegisterEvent event, Consumer<RegistrationCallback<T>> consumer) {
            consumer.accept((registry, id, object) -> event.register(registry.key(), id, () -> object));
        }
    }
}