package net.modgarden.barricade.mixin;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import static net.modgarden.barricade.Barricade.MIXIN_NOT_APPLIED;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccessor {
	@Invoker
	static <B extends Block> RecordCodecBuilder<B, BlockBehaviour.Properties> invokePropertiesCodec() {
		throw new IllegalStateException(MIXIN_NOT_APPLIED);
	}
}
