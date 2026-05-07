package net.modgarden.barricade.item;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;

import net.modgarden.barricade.BarricadeMod;
import net.modgarden.barricade.block.BarricadeBlock;
import net.modgarden.barricade.data.BarricadeData;
import net.modgarden.barricade.data.BlockedDirections;
import net.modgarden.barricade.registry.BarricadeComponents;
import org.jetbrains.annotations.NotNull;

public class BarricadeBlockItem extends EntityCheckBarrierBlockItem {
	public BarricadeBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
		ItemStack stack = context.getItemInHand();
		boolean collision;
		if (!stack.has(BarricadeComponents.BARRICADE)) {
			CollisionContext ctx = context.getPlayer() == null ? CollisionContext.empty() : CollisionContext.of(context.getPlayer());
			collision = context.getLevel().isUnobstructed(state, context.getClickedPos(), ctx);
		} else {
			Holder<BarricadeData> advancedBarrierHolder = stack.get(BarricadeComponents.BARRICADE);
			assert advancedBarrierHolder != null; // We already checked if it's present.
			BarricadeData barricadeData = advancedBarrierHolder.value();

			BlockedDirections directions = barricadeData.directions();

			collision = context.getLevel().getEntities(null, Shapes.block().move(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ()).bounds()).stream().allMatch(entity -> {
				CollisionContext ctx = CollisionContext.of(entity);
				boolean meetsCondition = false;
				try {
					meetsCondition = barricadeData.test(context.getLevel(), entity, state, context.getClickedPos());
				} catch (Exception e) {
					BarricadeMod.LOG.error("Failed to test placement ability", e);
				}
				boolean directionBlocks = directions.doesNotBlock() || !directions.shouldBlock(context.getClickedPos(), ctx);
				return barricadeData.condition().isEmpty() || !meetsCondition && directionBlocks;
			});
		}

		return (!mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && collision;
	}

	@Override
	public @NotNull Component getName(ItemStack stack) {
		//noinspection DataFlowIssue
		if (stack.has(BarricadeComponents.BARRICADE) && stack.get(BarricadeComponents.BARRICADE).value().name().isPresent()) {
			//noinspection DataFlowIssue
			return stack.get(BarricadeComponents.BARRICADE).value().name().get();
		}
		return super.getName(stack);
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		InteractionResult placeResult = super.place(context);

		Level level = context.getLevel();
		if (!placeResult.equals(InteractionResult.SUCCESS)) return placeResult;

		ItemStack stack = context.getItemInHand();

		if (!stack.has(BarricadeComponents.BARRICADE)) {
			BarricadeMod.LOG.error("Could not find Barricade component in Barricade item");
			return InteractionResult.FAIL;
		}

		Holder<BarricadeData> barricadeDataHolder = Objects.requireNonNull(stack.get(BarricadeComponents.BARRICADE));
		BlockPos pos = context.getClickedPos();
		BarricadeBlock.setBarricadeData(level, pos, barricadeDataHolder);

		return placeResult;
	}
}
