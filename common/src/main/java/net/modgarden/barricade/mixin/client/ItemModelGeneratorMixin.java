package net.modgarden.barricade.mixin.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {
    @Shadow protected abstract List<BlockElement> processFrames(int tintIndex, String texture, SpriteContents sprite);

    @Inject(method = "generateBlockModel", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void barricade$useBarricadeLayers(Function<Material, TextureAtlasSprite> spriteGetter, BlockModel model, CallbackInfoReturnable<BlockModel> cir, Map<String, Either<Material, String>> map, List<BlockElement> list) {
        for (int i = 0; i < BarricadeClient.BARRICADE_LAYERS.size(); i++) {
            String s = BarricadeClient.BARRICADE_LAYERS.get(i);
            if (!model.hasTexture(s)) {
                break;
            }

            Material material = model.getMaterial(s);
            map.put(s, Either.left(material));
            SpriteContents spritecontents = spriteGetter.apply(material).contents();
            list.addAll(Barricade.getHelper().fixSeamsOnNeoForge(processFrames(i, s, spritecontents), material.sprite()));
        }
    }
}
