package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record TrimMaterialData(ResourceLocation materialId, Map<ResourceLocation, String> overrides) {

    public static final Codec<Map<ResourceLocation, String>> OVERRIDE_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.STRING.validate(string ->
            ResourceLocation.isValidPath(string) ? DataResult.success(string) : DataResult.error(() -> "Non [a-z0-9/._-] character in override path: " + string))
    );

    public static final Codec<TrimMaterialData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("material").forGetter(TrimMaterialData::materialId),
            OVERRIDE_CODEC.optionalFieldOf("overrides", Map.of()).forGetter(TrimMaterialData::overrides)
    ).apply(instance, TrimMaterialData::new));

    public String getName(@Nullable ResourceLocation equipmentMaterial) {
        if (equipmentMaterial != null) {
            String overrideName = overrides.get(equipmentMaterial);

            if (overrideName != null) {
                return overrideName;
            }
        }
        return materialId.getPath();
    }
}
