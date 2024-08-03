package net.modgarden.barricade.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.modgarden.barricade.block.EntityBarrierBlock;
import net.modgarden.barricade.component.BlockedDirectionsComponent;
import net.modgarden.barricade.component.BlockedEntitiesComponent;
import net.modgarden.barricade.registry.BarricadeBlockEntityTypes;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.Nullable;

public class EntityBarrierBlockEntity extends BlockEntity {
    @Nullable
    private BlockedDirectionsComponent blockedDirections;
    private BlockedEntitiesComponent blockedEntities;

    public EntityBarrierBlockEntity(BlockPos pos, BlockState blockState) {
        super(BarricadeBlockEntityTypes.ENTITY_BARRIER, pos, blockState);
    }

    @Nullable
    public BlockedDirectionsComponent getBlockedDirections() {
        return blockedDirections;
    }

    @Nullable
    public BlockedEntitiesComponent getBlockedEntities() {
        return blockedEntities;
    }

    @Nullable
    public ResourceLocation getBackTextureLocation() {
        if (blockedEntities == null)
            return null;
        return blockedEntities.backTextureLocation();
    }

    public boolean matches(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof EntityBarrierBlock && stack.getOrDefault(BarricadeComponents.BLOCKED_ENTITIES, BlockedEntitiesComponent.EMPTY).equals(blockedEntities) && (stack.getOrDefault(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.EMPTY) == BlockedDirectionsComponent.EMPTY && blockedDirections == null || stack.getOrDefault(BarricadeComponents.BLOCKED_DIRECTIONS, BlockedDirectionsComponent.EMPTY).equals(blockedDirections));
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput components) {
        blockedDirections = components.get(BarricadeComponents.BLOCKED_DIRECTIONS);
        blockedEntities = components.get(BarricadeComponents.BLOCKED_ENTITIES);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        components.set(BarricadeComponents.BLOCKED_DIRECTIONS, blockedDirections);
        components.set(BarricadeComponents.BLOCKED_ENTITIES, blockedEntities);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("blocked_directions"))
            blockedDirections = BlockedDirectionsComponent.CODEC.parse(NbtOps.INSTANCE, tag.get("blocked_directions")).getOrThrow();
        if (tag.contains("blocked_entities"))
            blockedEntities = BlockedEntitiesComponent.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("blocked_entities")).mapOrElse(success -> success, err -> BlockedEntitiesComponent.EMPTY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (blockedDirections != null)
            tag.put("blocked_directions", BlockedDirectionsComponent.CODEC.encodeStart(NbtOps.INSTANCE, blockedDirections).getOrThrow());
        if (blockedEntities != null)
            tag.put("blocked_entities", BlockedEntitiesComponent.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), blockedEntities).getOrThrow());
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
