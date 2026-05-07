package net.modgarden.barricade.mixin;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.modgarden.barricade.data.ClearableIdMapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.IdMapper;

@Mixin(IdMapper.class)
public abstract class Mixin_IdMapper<T> implements ClearableIdMapper {
	@Shadow
	private int nextId;

	@Shadow
	@Final
	private Reference2IntMap<T> tToId;

	@Shadow
	@Final
	private List<T> idToT;

	@Override
	public void barricade$clear() {
		this.nextId = 0;
		this.tToId.clear();
		this.idToT.clear();
	}
}
