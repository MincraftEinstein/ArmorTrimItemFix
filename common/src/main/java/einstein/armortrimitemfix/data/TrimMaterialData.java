package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record TrimMaterialData(ResourceLocation materialId, Map<ResourceLocation, String> overrides) {

    public static final Codec<TrimMaterialData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("material").forGetter(TrimMaterialData::materialId),
            Codec.unboundedMap(ResourceLocation.CODEC, Codec.STRING).optionalFieldOf("overrides", Map.of()).forGetter(TrimMaterialData::overrides)
    ).apply(instance, TrimMaterialData::new));

    public String getName(ResourceLocation equipmentMaterial) {
        if (equipmentMaterial != null) {
            String overrideName = overrides.get(equipmentMaterial);

            if (overrideName != null) {
                return overrideName;
            }
        }
        return materialId.getPath();
    }
}
