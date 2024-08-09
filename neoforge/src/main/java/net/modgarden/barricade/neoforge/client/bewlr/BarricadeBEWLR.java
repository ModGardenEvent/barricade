package net.modgarden.barricade.neoforge.client.bewlr;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.modgarden.barricade.client.renderer.item.AdvancedBarrierItemRenderer;

public class BarricadeBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final BarricadeBEWLR INSTANCE = new BarricadeBEWLR();

    public BarricadeBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        AdvancedBarrierItemRenderer.renderItem(stack, transformType, poseStack, bufferSource, light, overlay);
    }
}