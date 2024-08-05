package net.modgarden.barricade.platform;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.ClientHooks;

import java.util.Collection;
import java.util.List;

public class BarricadePlatformHelperNeoForge implements BarricadePlatformHelper {

    @Override
    public Platform getPlatform() {

        return Platform.NEOFORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public <T> Collection<T> fixSeamsOnNeoForge(Collection<T> collection, Object textureAtlasSprite) {
        return (Collection<T>) ClientHooks.fixItemModelSeams((List<BlockElement>) collection, (TextureAtlasSprite) textureAtlasSprite);
    }
}