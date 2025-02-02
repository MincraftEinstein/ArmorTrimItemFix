package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record TrimMaterialData(ResourceLocation materialId, Map<ResourceLocation, ResourceLocation> overrides) {

    public static final Codec<TrimMaterialData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("material").forGetter(TrimMaterialData::materialId),
            Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).optionalFieldOf("overrides", Map.of()).forGetter(TrimMaterialData::overrides)
    ).apply(instance, TrimMaterialData::new));

    public String getFileName(ResourceLocation overrideId) {
        if (overrideId != null) {
            ResourceLocation overrideMaterial = overrides.get(overrideId);

            if (overrideMaterial != null) {
                return overrideMaterial.toDebugFileName();
            }
        }
        return materialId.toDebugFileName();
    }
}
