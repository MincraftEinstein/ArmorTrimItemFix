package einstein.armortrimitemfix;

import einstein.armortrimitemfix.api.TrimRegistry;
import einstein.armortrimitemfix.compat.AbstractTrimCompat;
import einstein.armortrimitemfix.compat.MinecraftCompat;
import einstein.armortrimitemfix.platform.Services;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.TrimPatterns;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraft.world.item.armortrim.ArmorTrim.TAG_TRIM_ID;

public class ArmorTrimItemFix {

    public static final String MOD_ID = "armortrimitemfix";
    public static final String MOD_NAME = "ArmorTrimItemFix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final float DEFAULT_TRIM_VALUE = 0.0000001F;
    public static final ResourceLocation TRIM_PATTERN_PREDICATE_ID = loc("trim_pattern");
    public static final ResourceLocation TRIM_MATERIAL_PREDICATE_ID = loc("trim_material");
    public static final ResourceLocation COLOR_PALETTE_KEY = new ResourceLocation("minecraft:trims/color_palettes/trim_palette");
    public static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("minecraft:blocks");

    public static final Map<Item, String> TRIMMABLES = new HashMap<>();
    public static final Map<ResourceLocation, Float> TRIM_PATTERNS = new HashMap<>();
    public static final Map<String, ResourceLocation> PERMUTATIONS = new HashMap<>();
    public static final TreeSet<MaterialData> TRIM_MATERIALS = new TreeSet<>();

    public static void init() {
    }

    public static void clientSetup() {
        registerCompat("minecraft", MinecraftCompat::new);
        for (Item trimmable : ArmorTrimItemFix.TRIMMABLES.keySet()) {
            ArmorTrimItemFix.registerArmorTrimProperty(trimmable);
        }
    }

    public static void registerArmorTrimProperty(Item item) {
        ItemProperties.register(item, TRIM_PATTERN_PREDICATE_ID, (stack, level, entity, seed) -> {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(TAG_TRIM_ID)) {
                CompoundTag trimTag = tag.getCompound(TAG_TRIM_ID);
                if (trimTag.contains("pattern")) {
                    String pattern = trimTag.getString("pattern");
                    Float value = TRIM_PATTERNS.get(ResourceLocation.tryParse(pattern));
                    return value == null ? DEFAULT_TRIM_VALUE : value;
                }
            }
            return 0;
        });

        ItemProperties.register(item, TRIM_MATERIAL_PREDICATE_ID, (stack, level, entity, seed) -> {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(TAG_TRIM_ID)) {
                CompoundTag trimTag = tag.getCompound(TAG_TRIM_ID);
                if (trimTag.contains("material")) {
                    ResourceLocation material = ResourceLocation.tryParse(trimTag.getString("material"));
                    if (material != null) {
                        for (MaterialData data : TRIM_MATERIALS) {
                            if (data.materialId().equals(material)) {
                                return data.propertyValue;
                            }
                        }
                    }
                }
            }
            return 0;
        });
    }

    public static ResourceLocation overrideName(ResourceLocation item, String patternName, String materialName) {
        return loc(item.getPath() + "_" + patternName + "_" + materialName + "_trim");
    }

    public static ResourceLocation vanillaOverrideName(ResourceLocation item, String materialName) {
        return new ResourceLocation(item.getPath() + "_" + materialName + "_trim");
    }

    public static ResourceLocation layerLoc(String prefix, String patternName, String materialName) {
        return loc("trims/items/" + prefix + "_" + patternName + "_trim_" + materialName);
    }

    public static boolean isDoubleLayered(Item item) {
        return item == Items.LEATHER_HELMET || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_BOOTS;
    }

    public static String getLayer(Item item) {
        return "layer" + (isDoubleLayered(item) ? 2 : 1);
    }

    public static <T extends AbstractTrimCompat> void registerCompat(String modId, Function<String, T> function) {
        if (Services.PLATFORM.isModLoaded(modId)) {
            function.apply(modId).init(TrimRegistry.INSTANCE);
        }
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static <K, V> Map<K, V> createValueSortedMap(Map<K, V> map, Comparator<V> comparator) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue(comparator))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static List<ItemOverride> createOverrides(BlockModel model, List<ItemOverride> overrides) {
        ResourceLocation modelLoc = ResourceLocation.tryParse(model.name);
        if (modelLoc != null) {
            String path = modelLoc.getPath();
            ResourceLocation itemId = path.contains("/") ? new ResourceLocation(modelLoc.getNamespace(), path.substring(path.lastIndexOf("/") + 1)) : modelLoc;
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item instanceof ArmorItem armor && ArmorTrimItemFix.TRIMMABLES.containsKey(armor)) {
                overrides.clear();
                Map<ResourceLocation, Float> patterns = createValueSortedMap(ArmorTrimItemFix.TRIM_PATTERNS, Float::compareTo);
                for (ArmorTrimItemFix.MaterialData data : ArmorTrimItemFix.TRIM_MATERIALS) {
                    String materialName = data.getName(item);
                    ItemOverride.Predicate materialPredicate = new ItemOverride.Predicate(TRIM_MATERIAL_PREDICATE_ID, data.propertyValue());
                    overrides.add(new ItemOverride(ArmorTrimItemFix.vanillaOverrideName(itemId, materialName).withPrefix("item/"), List.of(
                            new ItemOverride.Predicate(ArmorTrimItemFix.TRIM_PATTERN_PREDICATE_ID, DEFAULT_TRIM_VALUE), materialPredicate)));

                    patterns.forEach((pattern, value) ->
                            overrides.add(new ItemOverride(ArmorTrimItemFix.overrideName(itemId, pattern.getPath(), materialName).withPrefix("item/"), List.of(
                                    new ItemOverride.Predicate(ArmorTrimItemFix.TRIM_PATTERN_PREDICATE_ID, value), materialPredicate))));
                }
            }
        }

        return overrides;
    }

    public record MaterialData(ResourceLocation materialId, float propertyValue,
                               List<ArmorMaterial> armorMaterials) implements Comparable<MaterialData> {

        public String getName(Item item) {
            if (item instanceof ArmorItem armorItem && !armorMaterials.isEmpty() && armorMaterials.contains(armorItem.getMaterial())) {
                return materialId.getPath() + "_darker";
            }
            return materialId.getPath();
        }

        @Override
        public int compareTo(@NotNull MaterialData o) {
            return Float.compare(propertyValue(), o.propertyValue());
        }
    }
}