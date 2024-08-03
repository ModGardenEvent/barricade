package net.modgarden.barricade;


import net.modgarden.barricade.platform.ExamplePlatformHelperNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Barricade.MOD_ID)
public class ExampleModNeoForge {
    public ExampleModNeoForge(IEventBus eventBus) {
        Barricade.init();
        Barricade.setHelper(new ExamplePlatformHelperNeoForge());
    }
}