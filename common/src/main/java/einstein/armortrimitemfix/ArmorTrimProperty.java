package einstein.armortrimitemfix;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;

public class ArmorTrimProperty implements SelectItemModelProperty<ArmorTrimProperty.Data> {

    public static final Type<ArmorTrimProperty, Data> TYPE = Type.create(MapCodec.unit(new ArmorTrimProperty()), Data.CODEC);

    @Override
    public @Nullable Data get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext context) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) {
            return null;
        }

        return new Data(
                trim.pattern().unwrapKey().orElse(null),
                trim.material().unwrapKey().orElse(null)
        );
    }

    @Override
    public Type<ArmorTrimProperty, Data> type() {
        return TYPE;
    }

    public record Data(ResourceKey<TrimPattern> pattern, ResourceKey<TrimMaterial> material) {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceKey.codec(Registries.TRIM_PATTERN).fieldOf("pattern").forGetter(Data::pattern),
                ResourceKey.codec(Registries.TRIM_MATERIAL).fieldOf("material").forGetter(Data::material)
        ).apply(instance, Data::new));

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Data(
                    ResourceKey<TrimPattern> dataPattern, ResourceKey<TrimMaterial> dataMaterial
            )) {
                return pattern.equals(dataPattern) && material.equals(dataMaterial);
            }
            return false;
        }
    }
}
