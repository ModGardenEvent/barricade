package dev.greenhouseteam.examplemod;


import dev.greenhouseteam.examplemod.platform.ExamplePlatformHelperNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ExampleMod.MOD_ID)
public class ExampleModNeoForge {

    public ExampleModNeoForge(IEventBus eventBus) {
        ExampleMod.init();
        ExampleMod.setHelper(new ExamplePlatformHelperNeoForge());
    }
}