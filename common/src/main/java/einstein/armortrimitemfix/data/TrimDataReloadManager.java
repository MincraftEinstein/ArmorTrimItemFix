package einstein.armortrimitemfix.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static einstein.armortrimitemfix.ArmorTrimItemFix.*;

public class TrimDataReloadManager {

    public static final List<TrimMaterialData> TRIM_MATERIALS = new ArrayList<>();
    public static final List<Identifier> TRIM_PATTERNS = new ArrayList<>();
    public static final List<TrimmableItemData> TRIMMABLE_ITEMS = new ArrayList<>();

    private static final FileToIdConverter TRIM_MATERIALS_LISTER = ArmorTrimItemFix.createLister("materials");
    private static final FileToIdConverter TRIMMABLE_ITEMS_LISTER = ArmorTrimItemFix.createLister("trimmables");

    public static void loadMaterials(ResourceManager manager) {
        Map<Identifier, TrimMaterialData> resources = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(manager, TRIM_MATERIALS_LISTER, JsonOps.INSTANCE, TrimMaterialData.CODEC, resources);
        TRIM_MATERIALS.clear();
        TRIM_MATERIALS.addAll(resources.values());
    }

    public static void loadPatterns(ResourceManager manager) {
        List<Identifier> patterns = new ArrayList<>();
        Identifier jsonId = id(MOD_ID + "/patterns.json");

        for (Resource resource : manager.getResourceStack(jsonId)) {
            try (Reader reader = resource.openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                TrimPatternData data = TrimPatternData.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, element)).getOrThrow();

                if (data.replace()) {
                    patterns.clear();
                }

                data.values().forEach(pattern -> {
                    if (!patterns.contains(pattern)) {
                        patterns.add(pattern);
                    }
                });
            }
            catch (Exception e) {
                LOGGER.error("Couldn't read trim pattern list {} in data pack {}", jsonId, resource.sourcePackId(), e);
            }
        }

        TRIM_PATTERNS.clear();
        TRIM_PATTERNS.addAll(patterns);
    }

    public static void loadItems(ResourceManager manager) {
        Map<Identifier, TrimmableItemData> resources = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(manager, TRIMMABLE_ITEMS_LISTER, JsonOps.INSTANCE, TrimmableItemData.CODEC, resources);
        TRIMMABLE_ITEMS.clear();
        TRIMMABLE_ITEMS.addAll(resources.values());
    }
}
