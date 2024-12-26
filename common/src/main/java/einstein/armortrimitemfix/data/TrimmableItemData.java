package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;

public record TrimmableItemData(Item item, EquipmentType type, Optional<ResourceLocation> overrideMaterial, List<TextureLayer> layers, List<ItemTintSource> tintSources) {

    public static final Codec<TrimmableItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(TrimmableItemData::item),
            EquipmentType.CODEC.fieldOf("type").forGetter(TrimmableItemData::type),
            ResourceLocation.CODEC.optionalFieldOf("override_material").forGetter(TrimmableItemData::overrideMaterial),
            Codec.list(TextureLayer.CODEC).optionalFieldOf("layers", List.of()).forGetter(TrimmableItemData::layers),
            Codec.list(ItemTintSources.CODEC).optionalFieldOf("tints", List.of()).forGetter(TrimmableItemData::tintSources)
    ).apply(instance, TrimmableItemData::new));

    public record TextureLayer(int index, ResourceLocation texture) {

        public static final Codec<TextureLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 4).fieldOf("index").forGetter(TextureLayer::index),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(TextureLayer::texture)
        ).apply(instance, TextureLayer::new));
    }
}
