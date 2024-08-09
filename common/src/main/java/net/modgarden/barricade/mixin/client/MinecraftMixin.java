package net.modgarden.barricade.mixin.client;

import net.minecraft.client.Minecraft;
import net.modgarden.barricade.client.renderer.block.AdvancedBarrierBlockRenderer;
import net.modgarden.barricade.client.renderer.item.AdvancedBarrierItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"))
    private void barricade$resetAdvancedBarrierModels(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        AdvancedBarrierBlockRenderer.clearModelMap();
        AdvancedBarrierItemRenderer.clearModelMap();
    }
}
