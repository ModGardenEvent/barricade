package net.modgarden.barricade.registry;

import static net.modgarden.barricade.BarricadeMod.id;

import lgbt.greenhouse.silicate.api.SilicateBuiltInRegistries;
import lgbt.greenhouse.silicate.api.type.ValueType;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Avatar;

public final class BarricadeValueTypes {
	public static final ValueType<AgeableMob> PASSIVE_MOB = register(
			"passive_mob",
			AgeableMob.class
	);
	public static final ValueType<Avatar> AVATAR = register(
			"avatar",
			Avatar.class
	);

	private BarricadeValueTypes() {
	}

	public static void initialize() {
	}

	private static <T> ValueType<T> register(String path, Class<T> clazz) {
		return Registry.register(SilicateBuiltInRegistries.VALUE_TYPE, id(path), new ValueType<>(clazz, null));
	}
}
