package net.modgarden.barricade.client.renderer.item;

import net.minecraft.resources.Identifier;
import net.modgarden.barricade.Barricade;

public class AdvancedBarrierItemRenderer {
	public static final Identifier NO_BARRIER_TEXTURE = Barricade.asResource("item/barricade/no_barrier");
	public static final Identifier BARRIER_TEXTURE = Identifier.withDefaultNamespace("item/barrier");

	public static final Identifier DIRECTION_UP_TEXTURE = Barricade.asResource("item/barricade/direction/up");
	public static final Identifier DIRECTION_DOWN_TEXTURE = Barricade.asResource("item/barricade/direction/down");
	public static final Identifier DIRECTION_NORTH_TEXTURE = Barricade.asResource("item/barricade/direction/north");
	public static final Identifier DIRECTION_SOUTH_TEXTURE = Barricade.asResource("item/barricade/direction/south");
	public static final Identifier DIRECTION_WEST_TEXTURE = Barricade.asResource("item/barricade/direction/west");
	public static final Identifier DIRECTION_EAST_TEXTURE = Barricade.asResource("item/barricade/direction/east");
}
