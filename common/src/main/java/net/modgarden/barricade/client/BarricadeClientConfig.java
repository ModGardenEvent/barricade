package net.modgarden.barricade.client;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import house.greenhouse.greenhouseconfig.api.codec.GreenhouseConfigCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.modgarden.barricade.client.util.dfu.fix.V1ToV2FieldsFix;
import net.modgarden.barricade.client.util.dfu.schema.ConfigSchema;

import java.util.List;
import java.util.Set;

public record BarricadeClientConfig(boolean everythingVisible, Set<Either<ResourceLocation, ResourceKey<Block>>> visibleBlocks) {
    public static final BarricadeClientConfig DEFAULT = new BarricadeClientConfig(false, Set.of());
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
                            Codec.either(Codec.STRING.comapFlatMap(s -> {
                                if (s.startsWith("#"))
                                    return DataResult.success(ResourceLocation.tryParse(s.substring(1)));
                                return DataResult.error(() -> "Not an operator block tag");
                            }, rl -> "#" + rl.toString()), ResourceKey.codec(Registries.BLOCK)).listOf().xmap(Set::copyOf, List::copyOf),
                            "Which operator blocks are visible to you, not accounting the item you are holding.",
                            "Accepts a list of mixed block ids or operator blocks tags found within assets/<namespace>/barricade/operator_blocks/<path>.json",
                            "The player must be in creative and have at least permission level 2 to see barriers."
                    ), "visible_blocks", DEFAULT.visibleBlocks()
            ).forGetter(BarricadeClientConfig::visibleBlocks)
    ).apply(inst, BarricadeClientConfig::new));

    public static class Fixer {
        public static final DataFixer INSTANCE = createFixer();

        private static DataFixer createFixer() {
            DataFixerBuilder builder = new DataFixerBuilder(3);
            builder.addSchema(1, ConfigSchema::new);
            Schema v2 = builder.addSchema(2, Schema::new);
            builder.addFixer(new V1ToV2FieldsFix(v2));
            return builder.build().fixer();
        }
    }
}
