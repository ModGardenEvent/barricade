package net.modgarden.barricade.item;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.modgarden.barricade.Barricade;
import net.modgarden.barricade.data.AdvancedBarrier;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.registry.BarricadeComponents;
import net.modgarden.silicate.api.exception.InvalidContextParameterException;
import org.jetbrains.annotations.NotNull;

public class AdvancedBarrierBlockItem extends EntityCheckBarrierBlockItem {
	public AdvancedBarrierBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
		ItemStack stack = context.getItemInHand();
		boolean collision;
		if (!stack.has(BarricadeComponents.ADVANCED_BARRIER)) {
			CollisionContext ctx = context.getPlayer() == null ? CollisionContext.empty() : CollisionContext.of(context.getPlayer());
			collision = context.getLevel().isUnobstructed(state, context.getClickedPos(), ctx);
		} else {
			Holder<AdvancedBarrier> advancedBarrierHolder = stack.get(BarricadeComponents.ADVANCED_BARRIER);
			assert advancedBarrierHolder != null; // We already checked if it's present.
			AdvancedBarrier advancedBarrier = advancedBarrierHolder.value();

			BlockedDirections directions = advancedBarrier.directions();

			collision = context.getLevel().getEntities(null, Shapes.block().move(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ()).bounds()).stream().allMatch(entity -> {
				CollisionContext ctx = CollisionContext.of(entity);
				boolean meetsCondition = false;
				try {
					meetsCondition = advancedBarrier.test(context.getLevel(), entity, state, context.getClickedPos());
				} catch (InvalidContextParameterException e) {
					Barricade.LOG.error("Failed to test placement ability", e);
				}
				boolean directionBlocks = directions.doesNotBlock() || !directions.shouldBlock(context.getClickedPos(), ctx);
				return advancedBarrier.condition().isEmpty() || !meetsCondition && directionBlocks;
			});
		}

		return (!mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && collision;
	}

	@Override
	public @NotNull Component getName(ItemStack stack) {
		//noinspection DataFlowIssue
		if (stack.has(BarricadeComponents.ADVANCED_BARRIER) && stack.get(BarricadeComponents.ADVANCED_BARRIER).value().name().isPresent()) {
			//noinspection DataFlowIssue
			return stack.get(BarricadeComponents.ADVANCED_BARRIER).value().name().get();
		}
		return super.getName(stack);
	}
}
