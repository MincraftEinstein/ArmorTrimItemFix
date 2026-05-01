package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;

public record TrimPatternData(List<Identifier> values, boolean replace) {

    public static final Codec<TrimPatternData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.listOf().fieldOf("values").forGetter(TrimPatternData::values),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(TrimPatternData::replace)
    ).apply(instance, TrimPatternData::new));
}
