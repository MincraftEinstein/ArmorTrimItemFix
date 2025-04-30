package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record TrimPatternData(List<ResourceLocation> values, boolean replace) {

    public static final Codec<TrimPatternData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("values").forGetter(TrimPatternData::values),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(TrimPatternData::replace)
    ).apply(instance, TrimPatternData::new));
}
