package einstein.armortrimitemfix;

import einstein.armortrimitemfix.api.TrimRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import static einstein.armortrimitemfix.ArmorTrimItemFix.*;

@ApiStatus.Internal
public class TrimRegistryImpl implements TrimRegistry {

    private static float PATTERN_VALUE = DEFAULT_TRIM_VALUE;
    private static float MATERIAL_VALUE = 0;
    private static final float INCREMENTER = 0.0000001F;

    @Override
    public void registerPattern(ResourceLocation trimId) {
        if (!TRIM_PATTERNS.containsKey(trimId)) {
            TRIM_PATTERNS.put(trimId, PATTERN_VALUE += INCREMENTER);
        }
        else {
            LOGGER.error("Trim pattern with id (" + trimId + ") is already registered");
        }
    }

    @Override
    public void registerMaterial(ResourceLocation materialId, ArmorMaterial... armorMaterials) {
        List<ArmorMaterial> materials = List.of(armorMaterials);
        String path = materialId.getPath();
        String modId = materialId.getNamespace();
        TRIM_MATERIALS.add(new MaterialData(materialId, MATERIAL_VALUE += INCREMENTER, materials));
        PERMUTATIONS.put(path, new ResourceLocation(modId, "trims/color_palettes/" + path));
        if (!materials.isEmpty()) {
            String darker = path + "_darker";
            PERMUTATIONS.put(darker, new ResourceLocation(modId, "trims/color_palettes/" + darker));
        }
    }

    @Override
    public void registerTrimmableItem(Item item, String texturePrefix) {
        TRIMMABLES.put(item, texturePrefix);
    }
}
