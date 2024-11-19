package net.modgarden.barricade.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.advancements.critereon.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.modgarden.barricade.Barricade;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(PlayerPredicate.class)
public abstract class PlayerPredicateMixin implements EntitySubPredicate {
	@Shadow @Final private GameTypePredicate gameType;

	@Shadow @Final private MinMaxBounds.Ints level;

	@Shadow @Final private Optional<EntityPredicate> lookingAt;

	@Shadow @Final private Map<ResourceLocation, PlayerPredicate.AdvancementPredicate> advancements;

	@Shadow @Final private List<PlayerPredicate.StatMatcher<?>> stats;

	@Shadow @Final private Object2BooleanMap<ResourceLocation> recipes;

	@Inject(
		method = "matches",
		at = @At("HEAD"),
		cancellable = true
	)
	private void matchLocalPlayer(
			Entity p_entity,
			ServerLevel level,
			Vec3 position,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (!(p_entity instanceof LocalPlayer localPlayer))
			return;
		
		MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
		if (gameMode == null)
			Barricade.LOG.error("LocalPlayer has no MultiPlayerGameMode!");
		
		if (gameMode != null && !this.gameType.matches(gameMode.getPlayerMode()))
			cir.setReturnValue(false);
		else if (!this.level.matches(localPlayer.experienceLevel))
			cir.setReturnValue(false);
		else {
			StatsCounter statsCounter = localPlayer.getStats();

			for (PlayerPredicate.StatMatcher<?> statMatcher : this.stats)
				if (!statMatcher.matches(statsCounter))
					cir.setReturnValue(false);

			RecipeBook recipeBook = localPlayer.getRecipeBook();

			for (Object2BooleanMap.Entry<ResourceLocation> entry : this.recipes.object2BooleanEntrySet())
				if (recipeBook.contains(entry.getKey()) != entry.getBooleanValue())
					cir.setReturnValue(false);

			if (!this.advancements.isEmpty())
				Barricade.LOG.error("Advancements are unsupported in client-side LootContext checks.");

			if (this.lookingAt.isPresent())
				Barricade.LOG.error("LookingAt conditions are unsupported in client-side LootContext checks.");

			cir.setReturnValue(true);
		}
	}
}
