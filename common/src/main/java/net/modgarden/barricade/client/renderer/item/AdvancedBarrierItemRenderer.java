package net.modgarden.barricade.client.renderer.item;

import net.minecraft.resources.ResourceLocation;
import net.modgarden.barricade.Barricade;

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
