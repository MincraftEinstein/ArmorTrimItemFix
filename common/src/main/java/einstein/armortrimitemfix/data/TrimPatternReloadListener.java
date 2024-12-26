package einstein.armortrimitemfix.data;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrimPatternReloadListener extends EarlyResourceReloadListener<TrimPatternData> {

    private static final FileToIdConverter LISTER = ArmorTrimItemFix.createLister("patterns");
    public static final List<TrimPatternData> TRIM_PATTERNS = new ArrayList<>();

    public TrimPatternReloadListener() {
        super(TrimPatternData.CODEC, LISTER);
    }

    @Override
    protected void earlyApply(Map<ResourceLocation, TrimPatternData> map, ResourceManager manager) {
        TRIM_PATTERNS.clear();
        TRIM_PATTERNS.addAll(map.values());
    }
}
