package net.modgarden.barricade.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.registry.BarricadeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @ModifyReturnValue(method = "getMarkerParticleTarget", at = @At(value = "RETURN", ordinal = 0))
    private Block barricade$setMarkerParticleTarget(Block original, @Local ItemStack itemStack, @Local Item item) {
        if (itemStack.is(BarricadeTags.ItemTags.BARRIERS) && item instanceof BlockItem)
            return null;
        return original;
    }
}
