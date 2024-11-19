package net.modgarden.barricade.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.Nullable;

public class AdvancedBarrierBlockEntity extends BlockEntity implements Nameable {
    private Holder<AdvancedBarrier> data;

    public AdvancedBarrierBlockEntity(BlockPos pos, BlockState blockState) {
        super(BarricadeBlockEntityTypes.ADVANCED_BARRIER, pos, blockState);
        this.data = Holder.direct(AdvancedBarrier.DEFAULT);
    }

    public AdvancedBarrier getData() {
        if (data == null || !data.isBound())
            return AdvancedBarrier.DEFAULT;
        return data.value();
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput components) {
        data = components.get(BarricadeComponents.ADVANCED_BARRIER);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(BarricadeComponents.ADVANCED_BARRIER, data);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("data"))
            data = AdvancedBarrier.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("data")).getOrThrow();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (data != null && data.unwrapKey().isPresent())
            tag.put("data", AdvancedBarrier.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), data).getOrThrow());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public Component getName() {
        if (data.value().name().isEmpty())
            return Component.translatable("block.barricade.advanced_barrier");
        return data.value().name().get();
    }
}
