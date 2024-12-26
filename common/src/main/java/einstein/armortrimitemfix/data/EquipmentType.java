package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum EquipmentType implements StringRepresentable {

    HELMET("helmet"),
    CHESTPLATE("chestplate"),
    LEGGINGS("leggings"),
    BOOTS("boots");

    public static final Codec<EquipmentType> CODEC = StringRepresentable.fromEnum(EquipmentType::values);

    private final String name;

    EquipmentType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
