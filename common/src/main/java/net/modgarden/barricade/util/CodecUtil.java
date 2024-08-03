package net.modgarden.barricade.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Keyable;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.function.Supplier;

public class CodecUtil {
    public static final Codec<Object2BooleanOpenHashMap<Direction>> DIRECTION_MAP_CODEC = CodecUtil.enumMapCodec(Direction::values);
    public static final StreamCodec<ByteBuf, Object2BooleanOpenHashMap<Direction>> DIRECTION_MAP_STREAM_CODEC = CodecUtil.enumMapStreamCodec(Direction::values);

    public static <E extends Enum<E> & StringRepresentable> Codec<Object2BooleanOpenHashMap<E>> enumMapCodec(Supplier<E[]> elementsSupplier) {
        return Codec.simpleMap(StringRepresentable.fromEnum(elementsSupplier), Codec.BOOL, Keyable.forStrings(() -> Arrays.stream(elementsSupplier.get()).map(StringRepresentable::getSerializedName))).codec().flatComapMap(Object2BooleanOpenHashMap::new, DataResult::success);
    }

    public static <E extends Enum<E> & StringRepresentable> StreamCodec<ByteBuf, Object2BooleanOpenHashMap<E>> enumMapStreamCodec(Supplier<E[]> elementsSupplier) {
        return ByteBufCodecs.map(Object2BooleanOpenHashMap::new, ByteBufCodecs.fromCodec(StringRepresentable.fromEnum(elementsSupplier)), ByteBufCodecs.BOOL, 6);
    }
}
