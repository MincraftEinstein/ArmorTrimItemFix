package einstein.armortrimitemfix.data;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrimmableItemReloadListener extends EarlyResourceReloadListener<TrimmableItemData> {

    private static final FileToIdConverter LISTER = ArmorTrimItemFix.createLister("trimmables");
    public static final List<TrimmableItemData> TRIMMABLE_ITEMS = new ArrayList<>();

    public TrimmableItemReloadListener() {
        super(TrimmableItemData.CODEC, LISTER);
    }

    @Override
    protected void earlyApply(Map<ResourceLocation, TrimmableItemData> map, ResourceManager manager) {
        TRIMMABLE_ITEMS.clear();
        TRIMMABLE_ITEMS.addAll(map.values());
    }
}
