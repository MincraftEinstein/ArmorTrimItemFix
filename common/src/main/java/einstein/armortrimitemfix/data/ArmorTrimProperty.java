package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ArmorTrimProperty implements SelectItemModelProperty<ArmorTrimProperty.Data> {

    public static final Type<ArmorTrimProperty, Data> TYPE = Type.create(MapCodec.unit(new ArmorTrimProperty()), Data.CODEC);

    @Override
    public @Nullable Data get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext context) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) {
            return null;
        }

        return new Data(unwrapId(trim.pattern()), unwrapId(trim.material()));
    }

    @Override
    public Codec<Data> valueCodec() {
        return Data.CODEC;
    }

    private static @Nullable ResourceLocation unwrapId(Holder<?> holder) {
        ResourceKey<?> key = holder.unwrapKey().orElse(null);
        if (key != null) {
            return key.location();
        }
        return null;
    }

    @Override
    public Type<ArmorTrimProperty, Data> type() {
        return TYPE;
    }

    public record Data(@Nullable ResourceLocation pattern, @Nullable ResourceLocation material) {

        public static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("pattern").forGetter(Data::pattern),
                ResourceLocation.CODEC.fieldOf("material").forGetter(Data::material)
        ).apply(instance, Data::new));

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Data(
                    ResourceLocation dataPattern, ResourceLocation dataMaterial
            )) {
                return Objects.equals(pattern, dataPattern) && Objects.equals(material, dataMaterial);
            }
            return false;
        }
    }
}
