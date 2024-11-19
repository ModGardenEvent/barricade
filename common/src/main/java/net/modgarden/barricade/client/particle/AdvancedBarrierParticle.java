package net.modgarden.barricade.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.util.BarrierRenderUtils;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.particle.AdvancedBarrierParticleOptions;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.Optional;

public class AdvancedBarrierParticle extends TextureSheetParticle {
    private final BlockedDirections blockedDirections;
    private final BlockedDirections relative;
    private final ResourceLocation icon;
    private final Optional<BlockPos> origin;

    AdvancedBarrierParticle(BlockedDirections blockedDirections, @Nullable ResourceLocation icon, Optional<BlockPos> origin, ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.gravity = 0.0F;
        this.lifetime = 80;
        this.hasPhysics = false;
        this.blockedDirections = blockedDirections;
        this.icon = icon;
        this.origin = origin;
        this.relative = origin.map(pos -> BarrierRenderUtils.relativeDirectionsComponent(blockedDirections, pos)).orElse(blockedDirections);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        if (blockedDirections != null && origin.isPresent() && !blockedDirections.blocksAll() && !blockedDirections.doesNotBlock() && !relative.equals(BarrierRenderUtils.relativeDirectionsComponent(blockedDirections, origin.get()))) {
            if (age < 60)
                age = 60;
            return;
        }

        Quaternionf quaternionf = new Quaternionf();
        this.getFacingCameraMode().setRotation(quaternionf, renderInfo, partialTicks);
        if (this.roll != 0.0F) {
            quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }

        if (!relative.blocksAll()) {
            this.setSprite(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(Barricade.asResource("item/barricade/no_barrier")));
            this.renderRotatedQuad(buffer, renderInfo, quaternionf, partialTicks);
        }
        if (icon != null) {
            try {
                this.setSprite(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(icon));
            } catch (IllegalStateException ex) {
                this.setSprite(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(ResourceLocation.withDefaultNamespace("missingno")));
            }
            this.renderRotatedQuad(buffer, renderInfo, quaternionf, partialTicks);
        }
        if (!relative.blocksAll()) {
            for (Direction direction : relative.directions()) {
                this.setSprite(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(Barricade.asResource("item/barricade/direction/" + direction.getName())));
                this.renderRotatedQuad(buffer, renderInfo, quaternionf, partialTicks);
            }
        }

        if (relative.blocksAll()) {
            this.setSprite(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(ResourceLocation.withDefaultNamespace("item/barrier")));
            this.renderRotatedQuad(buffer, renderInfo, quaternionf, partialTicks);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return 0.5F;
    }

    public static class Provider implements ParticleProvider<AdvancedBarrierParticleOptions> {

        @Override
        public @Nullable Particle createParticle(AdvancedBarrierParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {

            return new AdvancedBarrierParticle(type.blockedDirections(), type.icon(), type.origin(), level, x, y, z);
        }
    }
}
