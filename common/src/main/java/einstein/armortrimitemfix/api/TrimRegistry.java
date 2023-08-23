package einstein.armortrimitemfix.api;

import einstein.armortrimitemfix.TrimRegistryImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface TrimRegistry {

    TrimRegistry INSTANCE = new TrimRegistryImpl();

    void registerPattern(ResourceLocation trimId);

    void registerMaterial(ResourceLocation materialId, ArmorMaterial... materials);

    void registerTrimmableItem(Item item, String texturePrefix);

    default void registerTrimmableItem(Item item) {
        ArmorItem armorItem = (ArmorItem)item;
        registerTrimmableItem(armorItem, armorItem.getType().getName());
    }
}
