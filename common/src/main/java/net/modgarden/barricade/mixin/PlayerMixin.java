package net.modgarden.barricade.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.model.CreativeOnlyBakedModelAccess;
import net.modgarden.barricade.client.util.BarrierRenderUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow private ItemStack lastItemInMainHand;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;lastItemInMainHand:Lnet/minecraft/world/item/ItemStack;", ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void barricade$refreshBarriersWhenSwapping(CallbackInfo ci, int i, double d0, double d1, ItemStack stack) {
        if (!level().isClientSide())
            return;
        if (stack.getItem() instanceof BlockItem blockItem && lastItemInMainHand.getItem() instanceof BlockItem lastBlockItem) {
            BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            BlockState lastState = lastItemInMainHand.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(lastBlockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof CreativeOnlyBakedModelAccess ^ Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(lastState) instanceof CreativeOnlyBakedModelAccess)
                BarrierRenderUtils.reloadBarrierBlocks();
        } else if (stack.getItem() instanceof BlockItem blockItem) {
            BlockState state = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof CreativeOnlyBakedModelAccess)
                BarrierRenderUtils.reloadBarrierBlocks();
        } else if (lastItemInMainHand.getItem() instanceof BlockItem blockItem) {
            BlockState state = lastItemInMainHand.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).apply(blockItem.getBlock().defaultBlockState());
            if (Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state) instanceof CreativeOnlyBakedModelAccess)
                BarrierRenderUtils.reloadBarrierBlocks();
        }
    }
}
