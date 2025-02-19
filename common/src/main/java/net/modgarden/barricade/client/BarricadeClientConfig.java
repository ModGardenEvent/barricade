package net.modgarden.barricade.client;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.greenhouseconfig.api.codec.GreenhouseConfigCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.client.util.OperatorBlockPseudoTag;

import java.util.List;

public record BarricadeClientConfig(boolean everythingVisible, List<Either<OperatorBlockPseudoTag, ResourceKey<Block>>> visibleBlocks) {
    public static final BarricadeClientConfig DEFAULT = new BarricadeClientConfig(false, List.of());
    public static final Codec<BarricadeClientConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GreenhouseConfigCodecs.defaultFieldCodec(
                    GreenhouseConfigCodecs.commentedCodec(
                            Codec.BOOL,
                            "If all invisible blocks are visible to you, not accounting the item you are holding.",
                            "The player must be in creative and have at least permission level 2 to see barriers."
                    ), "everything_visible", DEFAULT.everythingVisible()
            ).forGetter(BarricadeClientConfig::everythingVisible),
            GreenhouseConfigCodecs.defaultFieldCodec(
                    GreenhouseConfigCodecs.commentedCodec(
                            Codec.either(OperatorBlockPseudoTag.CODEC, ResourceKey.codec(Registries.BLOCK)).listOf(),
                            "Which operator blocks are visible to you, not accounting the item you are holding.",
                            "The player must be in creative and have at least permission level 2 to see barriers."
                    ), "visible_blocks", DEFAULT.visibleBlocks()
            ).forGetter(BarricadeClientConfig::visibleBlocks)
    ).apply(inst, BarricadeClientConfig::new));
}
