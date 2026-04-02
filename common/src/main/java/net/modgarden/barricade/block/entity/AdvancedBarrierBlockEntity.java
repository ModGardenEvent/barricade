package net.modgarden.barricade.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.barricade.registry.BarricadeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;

public class AdvancedBarrierBlockEntity extends BlockEntity implements Nameable {
	private Holder<AdvancedBarrier> data;

	public AdvancedBarrierBlockEntity(BlockPos pos, BlockState blockState) {
		super(BarricadeBlockEntityTypes.ADVANCED_BARRIER, pos, blockState);
		this.data = Holder.direct(AdvancedBarrier.DEFAULT);
	}

	public AdvancedBarrier getData() {
		// Gracefully handle invalid data
		if (data == null || !data.isBound())
			return AdvancedBarrier.DEFAULT;
		return data.value();
	}

	public Holder<AdvancedBarrier> getHolder() {
		return data;
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		data = components.get(BarricadeComponents.ADVANCED_BARRIER);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		components.set(BarricadeComponents.ADVANCED_BARRIER, data);
	}

	@Override
	protected void loadAdditional(ValueInput tag) {
		tag.read("data", AdvancedBarrier.CODEC).ifPresentOrElse((advancedBarrierHolder)->{
			data = advancedBarrierHolder;
		}, ()-> Barricade.LOG.error("Failed to load advanced data"));

		if (this.hasLevel()) {
			tag.read("a", AdvancedBarrier.CODEC).ifPresentOrElse((advancedBarrierHolder)->{
				try {
					data = this.getLevel().registryAccess().lookupOrThrow(BarricadeRegistries.ADVANCED_BARRIER).get(tag.getInt("a").orElseThrow()).orElseThrow();
				} catch (NoSuchElementException e) {
					Barricade.LOG.error("Unknown Advanced Barrier of ID {}", tag.getInt("a"));
				} catch (IllegalStateException e) {
					Barricade.LOG.error("Error occurred while fetching registry", e);
				}
			}, ()-> Barricade.LOG.error("Error occurred reading advanced data"));
		}
	}

	@Override
	protected void saveAdditional(ValueOutput tag) {
		if (data != null && data.unwrapKey().isPresent()) {
			tag.store("data", AdvancedBarrier.CODEC, data);
		}
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
		CompoundTag tag = new CompoundTag();
		if (this.hasLevel()) {
			assert this.getLevel() != null;
			Registry<AdvancedBarrier> registry = this.getLevel().registryAccess().lookupOrThrow(BarricadeRegistries.ADVANCED_BARRIER);
			tag.putInt("a", registry.getId(this.getData()));
		}

		return tag;
	}

	@Override
	public @NotNull Component getName() {
		if (data.value().name().isEmpty())
			return Component.translatable("block.barricade.advanced_barrier");
		return data.value().name().get();
	}
}
