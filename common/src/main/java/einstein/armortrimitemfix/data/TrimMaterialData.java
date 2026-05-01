package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Map;

public record TrimMaterialData(Identifier materialId, Map<Identifier, Identifier> overrides) {

    public static final Codec<TrimMaterialData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("material").forGetter(TrimMaterialData::materialId),
            Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).optionalFieldOf("overrides", Map.of()).forGetter(TrimMaterialData::overrides)
    ).apply(instance, TrimMaterialData::new));

    public String getFileName(Identifier overrideId) {
        if (overrideId != null) {
            Identifier overrideMaterial = overrides.get(overrideId);

            if (overrideMaterial != null) {
                return overrideMaterial.toDebugFileName();
            }
        }
        return materialId.toDebugFileName();
    }
}
