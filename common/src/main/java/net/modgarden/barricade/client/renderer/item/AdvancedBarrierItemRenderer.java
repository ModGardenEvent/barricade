package net.modgarden.barricade.client.renderer.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.client.BarricadeClient;
import net.modgarden.barricade.client.util.AdvancedBarrierModelValues;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.mixin.client.ModelBakeryAccessor;
import net.modgarden.barricade.registry.BarricadeComponents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdvancedBarrierItemRenderer {
	public static final ResourceLocation NO_BARRIER_TEXTURE = Barricade.asResource("item/barricade/no_barrier");
	public static final ResourceLocation BARRIER_TEXTURE = ResourceLocation.withDefaultNamespace("item/barrier");

	public static final ResourceLocation DIRECTION_UP_TEXTURE = Barricade.asResource("item/barricade/direction/up");
	public static final ResourceLocation DIRECTION_DOWN_TEXTURE = Barricade.asResource("item/barricade/direction/down");
	public static final ResourceLocation DIRECTION_NORTH_TEXTURE = Barricade.asResource("item/barricade/direction/north");
	public static final ResourceLocation DIRECTION_SOUTH_TEXTURE = Barricade.asResource("item/barricade/direction/south");
	public static final ResourceLocation DIRECTION_WEST_TEXTURE = Barricade.asResource("item/barricade/direction/west");
	public static final ResourceLocation DIRECTION_EAST_TEXTURE = Barricade.asResource("item/barricade/direction/east");
}
