package net.modgarden.barricade.client.bewlr;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.modgarden.barricade.client.renderer.item.DirectionalBarrierItemRenderer;
import net.modgarden.barricade.client.renderer.item.EntityBarrierItemRenderer;
import net.modgarden.barricade.item.EntityBarrierBlockItem;

public class BarricadeBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final BarricadeBEWLR INSTANCE = new BarricadeBEWLR();

    public BarricadeBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (stack.getItem() instanceof EntityBarrierBlockItem) {
            EntityBarrierItemRenderer.renderItem(stack, transformType, poseStack, bufferSource, light, overlay);
        } else {
            DirectionalBarrierItemRenderer.renderItem(stack, transformType, poseStack, bufferSource, light, overlay);
        }
    }

}
