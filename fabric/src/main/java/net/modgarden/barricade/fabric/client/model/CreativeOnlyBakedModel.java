package net.modgarden.barricade.fabric.client.model;

import com.mojang.datafixers.util.Either;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.model.OperatorBakedModelAccess;
import net.modgarden.barricade.client.util.OperatorItemPseudoTag;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CreativeOnlyBakedModel implements BakedModel, OperatorBakedModelAccess {
    private final BakedModel model;
    private final Either<ResourceLocation, ResourceKey<Item>> requiredItem;
    private OperatorItemPseudoTag cachedPseudoTag;

    public CreativeOnlyBakedModel(BakedModel model, Either<ResourceLocation, ResourceKey<Item>> requiredItem) {
        this.model = model;
        this.requiredItem = requiredItem;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockGetter, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        if (!RendererAccess.INSTANCE.hasRenderer() || (!Minecraft.getInstance().player.canUseGameMasterBlocks() || !Minecraft.getInstance().player.isHolding(stack -> requiredItem().map(tag -> tag.contains(stack.getItemHolder()), key -> stack.getItemHolder().is(key)))))
            return;

        QuadEmitter emitter = context.getEmitter();

        final RenderMaterial material = model.useAmbientOcclusion() ? RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(BlendMode.fromRenderLayer(ItemBlockRenderTypes.getRenderType(state, true))).find() : RendererAccess.INSTANCE.getRenderer().materialFinder().ambientOcclusion(TriState.FALSE).blendMode(BlendMode.fromRenderLayer(ItemBlockRenderTypes.getRenderType(state, true))).find();

        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            final Direction cullFace = ModelHelper.faceFromIndex(i);

            if (!context.hasTransform() && context.isFaceCulled(cullFace))
                continue;

            final List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get());

            for (final BakedQuad q : quads) {
                emitter.fromVanilla(q, material, cullFace);
                emitter.emit();
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        // Workaround for block display entities.
        return model.getQuads(state, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return model.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return model.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return model.getOverrides();
    }

    @Override
    public Either<OperatorItemPseudoTag, ResourceKey<Item>> requiredItem() {
        return requiredItem.mapBoth(id -> {
            if (cachedPseudoTag == null)
                cachedPseudoTag = OperatorItemPseudoTag.Registry.get(id);
            return cachedPseudoTag;
        }, Function.identity());
    }
}
