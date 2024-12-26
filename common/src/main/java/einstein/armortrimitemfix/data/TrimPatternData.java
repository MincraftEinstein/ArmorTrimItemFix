package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record TrimPatternData(ResourceLocation pattern) {

    public static final Codec<TrimPatternData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("pattern").forGetter(TrimPatternData::pattern)
    ).apply(instance, TrimPatternData::new));
}
