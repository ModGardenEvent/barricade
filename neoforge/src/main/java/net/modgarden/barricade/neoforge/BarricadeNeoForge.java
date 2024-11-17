package net.modgarden.barricade.neoforge;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.neoforge.platform.BarricadePlatformHelperNeoForge;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeBlocks;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.barricade.registry.BarricadeItems;
import net.modgarden.barricade.registry.internal.RegistrationCallback;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;
import java.util.function.Consumer;

@Mod(Barricade.MOD_ID)
public class BarricadeNeoForge {
    public BarricadeNeoForge(IEventBus eventBus) {
        Barricade.init(new BarricadePlatformHelperNeoForge());
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

        @SubscribeEvent
        public static void buildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
                if (!event.hasPermissions())
                    return;
                ItemStack startItem = null;
                for (ItemStack entry : event.getParentEntries()) {
                    if (entry.is(Items.BARRIER)) {
                        startItem = entry;
                        break;
                    }
                }
                if (startItem == null)
                    return;

                List<Item> items = List.of(
                        BarricadeItems.UP_BARRIER,
                        BarricadeItems.DOWN_BARRIER,
                        BarricadeItems.NORTH_BARRIER,
                        BarricadeItems.SOUTH_BARRIER,
                        BarricadeItems.WEST_BARRIER,
                        BarricadeItems.EAST_BARRIER,
                        BarricadeItems.HORIZONTAL_BARRIER,
                        BarricadeItems.VERTICAL_BARRIER,
                        BarricadeItems.PLAYER_BARRIER,
                        BarricadeItems.MOB_BARRIER,
                        BarricadeItems.PASSIVE_BARRIER,
                        BarricadeItems.HOSTILE_BARRIER
                );

                for (Item item : items) {
                    ItemStack stack = new ItemStack(item);
                    event.insertAfter(startItem, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    startItem = stack;
                }
            }
        }
    }
}