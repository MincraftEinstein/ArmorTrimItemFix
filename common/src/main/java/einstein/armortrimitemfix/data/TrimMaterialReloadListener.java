package einstein.armortrimitemfix.data;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrimMaterialReloadListener extends EarlyResourceReloadListener<TrimMaterialData> {

    public static final ResourceLocation ID = ArmorTrimItemFix.loc("trim_materials");
    private static final FileToIdConverter LISTER = ArmorTrimItemFix.createLister("materials");
    public static final List<TrimMaterialData> TRIM_MATERIALS = new ArrayList<>();

    public TrimMaterialReloadListener() {
        super(TrimMaterialData.CODEC, LISTER);
    }

    @Override
    protected void earlyApply(Map<ResourceLocation, TrimMaterialData> map, ResourceManager manager) {
        TRIM_MATERIALS.clear();
        TRIM_MATERIALS.addAll(map.values());
    }
}
