package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record TrimmableItemData(Item item, EquipmentType type, Optional<ResourceLocation> overrideMaterial,
                                Map<String, ResourceLocation> layers, List<ItemTintSource> tintSources) {

    private static final List<String> VALID_LAYERS = List.of("layer0", "layer1", "layer2", "layer3");

    private static final Codec<Map<String, ResourceLocation>> LAYER_CODEC = Codec.unboundedMap(Codec.STRING.validate(string ->
            VALID_LAYERS.contains(string) ? DataResult.success(string) : DataResult.error(() -> "Layer must be one of " + VALID_LAYERS)
    ), ResourceLocation.CODEC);

    public static final Codec<TrimmableItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(TrimmableItemData::item),
            EquipmentType.CODEC.fieldOf("type").forGetter(TrimmableItemData::type),
            ResourceLocation.CODEC.optionalFieldOf("override_material").forGetter(TrimmableItemData::overrideMaterial),
            LAYER_CODEC.optionalFieldOf("textures", Map.of()).forGetter(TrimmableItemData::layers),
            Codec.list(ItemTintSources.CODEC).optionalFieldOf("tints", List.of()).forGetter(TrimmableItemData::tintSources)
    ).apply(instance, TrimmableItemData::new));
}
