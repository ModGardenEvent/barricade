package net.modgarden.barricade.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.block.AdvancedBarrierBlock;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.block.entity.AdvancedBarrierBlockEntity;
import net.modgarden.barricade.client.particle.AdvancedBarrierParticle;
import net.modgarden.barricade.client.util.BarrierRenderUtils;
import net.modgarden.barricade.client.util.OperatorItemPseudoTag;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.registry.BarricadeItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Mutable
    @Shadow @Final private static Set<Item> MARKER_PARTICLE_ITEMS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void barricade$addToMarkerParticles(CallbackInfo ci) {
        Set<Item> set = new HashSet<>(MARKER_PARTICLE_ITEMS);
        set.add(BarricadeItems.ADVANCED_BARRIER);
        set.add(BarricadeItems.UP_BARRIER);
        set.add(BarricadeItems.DOWN_BARRIER);
        set.add(BarricadeItems.NORTH_BARRIER);
        set.add(BarricadeItems.SOUTH_BARRIER);
        set.add(BarricadeItems.WEST_BARRIER);
        set.add(BarricadeItems.EAST_BARRIER);
        set.add(BarricadeItems.HORIZONTAL_BARRIER);
        set.add(BarricadeItems.VERTICAL_BARRIER);
        set.add(BarricadeItems.PLAYER_BARRIER);
        set.add(BarricadeItems.MOB_BARRIER);
        set.add(BarricadeItems.PASSIVE_BARRIER);
        set.add(BarricadeItems.HOSTILE_BARRIER);
        MARKER_PARTICLE_ITEMS = Set.copyOf(set);
    }

    @ModifyReturnValue(method = "getMarkerParticleTarget", at = @At(value = "RETURN", ordinal = 0))
    private Block barricade$setMarkerParticleTarget(Block original, @Local ItemStack itemStack, @Local Item item) {
        if (Barricade.isOperatorModel(original.defaultBlockState()) || (original instanceof AdvancedBarrierBlock && Barricade.isOperatorModel(Blocks.BARRIER.defaultBlockState())))
            return null;
        if (OperatorItemPseudoTag.Registry.get(Barricade.asResource("barriers")).contains(item.builtInRegistryHolder()))
            return Blocks.BARRIER;
        return original;
    }

    @ModifyExpressionValue(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;", ordinal = 1))
    private Block barricade$trickGameIntoRendering(Block original) {
        if (OperatorItemPseudoTag.Registry.get(Barricade.asResource("barriers")).contains(original.asItem().builtInRegistryHolder()))
            return Blocks.BARRIER;
        return original;
    }

    @WrapOperation(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void barricade$renderVanillaStyle(ClientLevel instance, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, Operation<Void> original, @Local(argsOnly = true) Block block, @Local(argsOnly = true) BlockPos.MutableBlockPos blockPos, @Local BlockState blockState) {
        if (block != blockState.getBlock() && OperatorItemPseudoTag.Registry.get(Barricade.asResource("barriers")).contains(blockState.getBlock().asItem().builtInRegistryHolder())) {
            if (blockState.getBlock() instanceof DirectionalBarrierBlock directionalBarrierBlock) {
                BarrierRenderUtils.createAdvancedParticle(directionalBarrierBlock.directions(), null, particleOptions -> original.call(instance, particleOptions, x, y, z, xSpeed, ySpeed, zSpeed), blockPos.immutable());
                return;
            } else if (instance.getBlockEntity(blockPos.immutable()) instanceof AdvancedBarrierBlockEntity blockEntity) {
                BarrierRenderUtils.createAdvancedParticle(blockEntity.getBlockedDirections() == null ? BlockedDirectionsComponent.of(Direction.values()) : blockEntity.getBlockedDirections(), blockEntity.getBlockedEntities() == null ? null : blockEntity.getBlockedEntities().backTextureLocation(), particleOptions -> original.call(instance, particleOptions, x, y, z, xSpeed, ySpeed, zSpeed), blockPos.immutable());
                return;
            }
            original.call(instance, new BlockParticleOption(ParticleTypes.BLOCK_MARKER, blockState), x, y, z, xSpeed, ySpeed, zSpeed);
            return;
        }
        original.call(instance, particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }
}
