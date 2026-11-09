package einstein.armortrimitemfix.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.StrictJsonParser;

import java.io.IOException;
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
        scanDirectory(manager, TRIM_MATERIALS_LISTER, JsonOps.INSTANCE, TrimMaterialData.CODEC, resources);
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
        scanDirectory(manager, TRIMMABLE_ITEMS_LISTER, JsonOps.INSTANCE, TrimmableItemData.CODEC, resources);
        TRIMMABLE_ITEMS.clear();
        TRIMMABLE_ITEMS.addAll(resources.values());
    }
    public static <T> void scanDirectory(
            final ResourceManager manager, final FileToIdConverter lister, final DynamicOps<JsonElement> ops, final Codec<T> codec, final Map<Identifier, T> result
    ) {
        for (Map.Entry<Identifier, Resource> entry : lister.listMatchingResources(manager).entrySet()) {
            Identifier location = entry.getKey();
            Identifier id = lister.fileToId(location);

            try {
                Reader reader = entry.getValue().openAsReader();

                try {
                    codec.parse(ops, StrictJsonParser.parse(reader)).ifSuccess(parsed -> {
                        if (result.putIfAbsent(id, parsed) != null) {
                            throw new IllegalStateException("Duplicate data file ignored with ID " + id);
                        }
                    }).ifError(error -> ArmorTrimItemFix.LOGGER.error("Couldn't parse data file '{}' from '{}': {}", id, location, error));
                } catch (Throwable var13) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable var12) {
                            var13.addSuppressed(var12);
                        }
                    }

                    throw var13;
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (IllegalArgumentException | IOException | JsonParseException var14) {
                ArmorTrimItemFix.LOGGER.error("Couldn't parse data file '{}' from '{}'", id, location, var14);
            }
        }
    }

}