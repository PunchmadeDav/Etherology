package ru.feytox.etherology.magic.staff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.NonNull;
import net.minecraft.network.codec.PacketCodec;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record StaffPartInfo(@NonNull StaffPart part, @NonNull StaffPattern firstPattern,
                            @NonNull StaffPattern secondPattern) {

    public static StaffPartInfo of(@NonNull StaffPart part, @NonNull StaffPattern firstPattern) {
        return new StaffPartInfo(part, firstPattern, StaffPattern.EMPTY);
    }

    public static final Codec<StaffPartInfo> CODEC;
    public static final PacketCodec<ByteBuf, StaffPartInfo> PACKET_CODEC;

    public static List<StaffPartInfo> generateAll() {
        return Arrays.stream(StaffPart.values())
                .flatMap(part -> part.getFirstPatterns().get().stream()
                        .map(firstPattern -> part.getSecondPatterns().get().stream()
                                .map(secondPattern -> new StaffPartInfo(part, firstPattern, secondPattern))))
                .flatMap(Function.identity())
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                StaffPart.CODEC.fieldOf("part").forGetter(StaffPartInfo::part),
                StaffPatterns.CODEC.fieldOf("pattern1").forGetter(StaffPartInfo::firstPattern),
                StaffPatterns.CODEC.fieldOf("pattern2").forGetter(StaffPartInfo::secondPattern)
        ).apply(instance, StaffPartInfo::new));
        PACKET_CODEC = PacketCodec.tuple(StaffPart.PACKET_CODEC, StaffPartInfo::part,
                StaffPatterns.PACKET_CODEC, StaffPartInfo::firstPattern,
                StaffPatterns.PACKET_CODEC, StaffPartInfo::secondPattern, StaffPartInfo::new);
    }
}
