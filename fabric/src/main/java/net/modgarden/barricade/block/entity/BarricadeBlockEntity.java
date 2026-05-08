package net.modgarden.barricade.block.entity;

import java.util.Objects;
import java.util.Optional;

import net.modgarden.barricade.BarricadeFabric;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeRegistries;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class BarricadeBlockEntity extends BlockEntity {
	public int id = -1;
	private static final String ID_KEY = "id";

	public BarricadeBlockEntity(
			BlockPos worldPosition,
			BlockState blockState
	) {
		super(BarricadeBlockEntityTypes.BARRICADE, worldPosition, blockState);
	}

	@Override
	protected void loadAdditional(@NonNull ValueInput input) {
		if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT) && !Minecraft.getInstance().isLocalServer()) {
			return;
		}

		if (this.id == -1 && input.contains(ID_KEY)) {
			Optional<String> optionalString = input.getString(ID_KEY);

			if (optionalString.isPresent()) {
				Optional<Holder.Reference<BarricadeData>> holder = Objects.requireNonNull(level).registryAccess().lookupOrThrow(BarricadeRegistries.BARRICADE).get(Identifier.parse(optionalString.get()));

				//noinspection OptionalIsPresent
				if (holder.isPresent()) {
					this.id = BarricadeData.ID_MAPPER.getIdOrThrow(holder.get());
					BarricadeFabric.BARRICADES_TO_PROCESS.add(this);
				}
			} else {
				this.id = input.getIntOr(
						ID_KEY,
						BarricadeData.ID_MAPPER.getId(BarricadeData.UNKNOWN_HOLDER)
				);
				BarricadeFabric.BARRICADES_TO_PROCESS.add(this);
			}
		}
	}
}
