package net.modgarden.barricade.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.block.DirectionalBarrierBlock;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.Nullable;

public class DirectionalBarrierBlockEntity extends BlockEntity {
    public DirectionalBarrierBlockEntity(BlockPos pos, BlockState blockState) {
        super(BarricadeBlockEntityTypes.DIRECTIONAL_BARRIER, pos, blockState);
    }

    public boolean matches(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DirectionalBarrierBlock && stack.getOrDefault(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.EMPTY).matches(getBlockState());
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
}
