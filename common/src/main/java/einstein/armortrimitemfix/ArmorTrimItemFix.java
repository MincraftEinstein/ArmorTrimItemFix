package einstein.armortrimitemfix;

import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ArmorTrimItemFix {

    public static final String MOD_ID = "armortrimitemfix";
    public static final String MOD_NAME = "ArmorTrimItemFix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final ResourceLocation TRIM_PATTERN_PREDICATE_ID = loc("trim_pattern");
    public static final float DEFAULT_TRIM_VALUE = 0.001F;
    public static final ResourceLocation COLOR_PALETTE_KEY = new ResourceLocation("minecraft:trims/color_palettes/trim_palette");
    public static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("minecraft:blocks");
    public static final Map<String, ResourceLocation> PERMUTATIONS = Util.make(new HashMap<>(), map -> {
        map.put("quartz", new ResourceLocation("minecraft:trims/color_palettes/quartz"));
        map.put("iron", new ResourceLocation("minecraft:trims/color_palettes/iron"));
        map.put("gold", new ResourceLocation("minecraft:trims/color_palettes/gold"));
        map.put("diamond", new ResourceLocation("minecraft:trims/color_palettes/diamond"));
        map.put("netherite", new ResourceLocation("minecraft:trims/color_palettes/netherite"));
        map.put("redstone", new ResourceLocation("minecraft:trims/color_palettes/redstone"));
        map.put("copper", new ResourceLocation("minecraft:trims/color_palettes/copper"));
        map.put("emerald", new ResourceLocation("minecraft:trims/color_palettes/emerald"));
        map.put("lapis", new ResourceLocation("minecraft:trims/color_palettes/lapis"));
        map.put("amethyst", new ResourceLocation("minecraft:trims/color_palettes/amethyst"));
        map.put("iron_darker", new ResourceLocation("minecraft:trims/color_palettes/iron_darker"));
        map.put("gold_darker", new ResourceLocation("minecraft:trims/color_palettes/gold_darker"));
        map.put("diamond_darker", new ResourceLocation("minecraft:trims/color_palettes/diamond_darker"));
        map.put("netherite_darker", new ResourceLocation("minecraft:trims/color_palettes/netherite_darker"));
    });
    public static final Map<Item, ArmorItem.Type> TRIMMABLES = Util.make(new HashMap<>(), map -> {
        map.put(Items.LEATHER_HELMET, ArmorItem.Type.HELMET);
        map.put(Items.LEATHER_CHESTPLATE, ArmorItem.Type.CHESTPLATE);
        map.put(Items.LEATHER_LEGGINGS, ArmorItem.Type.LEGGINGS);
        map.put(Items.LEATHER_BOOTS, ArmorItem.Type.BOOTS);
        map.put(Items.CHAINMAIL_HELMET, ArmorItem.Type.HELMET);
        map.put(Items.CHAINMAIL_CHESTPLATE, ArmorItem.Type.CHESTPLATE);
        map.put(Items.CHAINMAIL_LEGGINGS, ArmorItem.Type.LEGGINGS);
        map.put(Items.CHAINMAIL_BOOTS, ArmorItem.Type.BOOTS);
        map.put(Items.IRON_HELMET, ArmorItem.Type.HELMET);
        map.put(Items.IRON_CHESTPLATE, ArmorItem.Type.CHESTPLATE);
        map.put(Items.IRON_LEGGINGS, ArmorItem.Type.LEGGINGS);
        map.put(Items.IRON_BOOTS, ArmorItem.Type.BOOTS);
        map.put(Items.GOLDEN_HELMET, ArmorItem.Type.HELMET);
        map.put(Items.GOLDEN_CHESTPLATE, ArmorItem.Type.CHESTPLATE);
        map.put(Items.GOLDEN_LEGGINGS, ArmorItem.Type.LEGGINGS);
        map.put(Items.GOLDEN_BOOTS, ArmorItem.Type.BOOTS);
        map.put(Items.DIAMOND_HELMET, ArmorItem.Type.HELMET);
        map.put(Items.DIAMOND_CHESTPLATE, ArmorItem.Type.CHESTPLATE);
        map.put(Items.DIAMOND_LEGGINGS, ArmorItem.Type.LEGGINGS);
        map.put(Items.DIAMOND_BOOTS, ArmorItem.Type.BOOTS);
        map.put(Items.NETHERITE_HELMET, ArmorItem.Type.HELMET);
        map.put(Items.NETHERITE_CHESTPLATE, ArmorItem.Type.CHESTPLATE);
        map.put(Items.NETHERITE_LEGGINGS, ArmorItem.Type.LEGGINGS);
        map.put(Items.NETHERITE_BOOTS, ArmorItem.Type.BOOTS);
        map.put(Items.TURTLE_HELMET, ArmorItem.Type.HELMET);
    });
    public static final TreeSet<MaterialData> TRIM_MATERIALS = Util.make(new TreeSet<>(), list -> {
        list.add(new MaterialData("quartz", 0.1F));
        list.add(new MaterialData("iron", 0.2F, ArmorMaterials.IRON, "iron_darker"));
        list.add(new MaterialData("netherite", 0.3F, ArmorMaterials.NETHERITE, "netherite_darker"));
        list.add(new MaterialData("redstone", 0.4F));
        list.add(new MaterialData("copper", 0.5F));
        list.add(new MaterialData("gold", 0.6F, ArmorMaterials.GOLD, "gold_darker"));
        list.add(new MaterialData("emerald", 0.7F));
        list.add(new MaterialData("diamond", 0.8F, ArmorMaterials.DIAMOND, "diamond_darker"));
        list.add(new MaterialData("lapis", 0.9F));
        list.add(new MaterialData("amethyst", 1.0F));
    });
    public static final Map<ResourceLocation, Float> TRIM_PATTERNS = createValueSortedMap(Util.make(new HashMap<>(), map -> {
        float f = 0;
        float f1 = 0.0625F;
        map.put(TrimPatterns.SENTRY.location(), f += f1);
        map.put(TrimPatterns.DUNE.location(), f += f1);
        map.put(TrimPatterns.COAST.location(), f += f1);
        map.put(TrimPatterns.WILD.location(), f += f1);
        map.put(TrimPatterns.WARD.location(), f += f1);
        map.put(TrimPatterns.EYE.location(), f += f1);
        map.put(TrimPatterns.VEX.location(), f += f1);
        map.put(TrimPatterns.TIDE.location(), f += f1);
        map.put(TrimPatterns.SNOUT.location(), f += f1);
        map.put(TrimPatterns.RIB.location(), f += f1);
        map.put(TrimPatterns.SPIRE.location(), f += f1);
        map.put(TrimPatterns.WAYFINDER.location(), f += f1);
        map.put(TrimPatterns.SHAPER.location(), f += f1);
        map.put(TrimPatterns.SILENCE.location(), f += f1);
        map.put(TrimPatterns.RAISER.location(), f += f1);
        map.put(TrimPatterns.HOST.location(), f += f1);
    }), Float::compareTo);

    public static void init() {
    }

    public static void clientSetup() {
        for (Item trimmable : ArmorTrimItemFix.TRIMMABLES.keySet()) {
            ArmorTrimItemFix.registerArmorTrimProperty(trimmable);
        }
    }

    public static void registerArmorTrimProperty(Item item) {
        ItemProperties.register(item, TRIM_PATTERN_PREDICATE_ID, (stack, level, entity, seed) -> {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(ArmorTrim.TAG_TRIM_ID)) {
                CompoundTag trimTag = tag.getCompound(ArmorTrim.TAG_TRIM_ID);
                if (trimTag.contains("pattern")) {
                    String pattern = trimTag.getString("pattern");
                    Float value = TRIM_PATTERNS.get(ResourceLocation.tryParse(pattern));
                    return value == null ? DEFAULT_TRIM_VALUE : value;
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

    public static ResourceLocation layerLoc(ArmorItem.Type armorType, String patternName, String materialName) {
        return loc("trims/items/" + armorType.getName() + "_" + patternName + "_trim_" + materialName);
    }

    public static boolean isDoubleLayered(Item item) {
        return item == Items.LEATHER_HELMET || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_BOOTS;
    }

    public static String getLayer(Item item) {
        return "layer" + (isDoubleLayered(item) ? 2 : 1);
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
                for (ArmorTrimItemFix.MaterialData data : ArmorTrimItemFix.TRIM_MATERIALS) {
                    String materialName = data.getName(item);
                    ItemOverride.Predicate materialPredicate = new ItemOverride.Predicate(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, data.propertyValue());
                    overrides.add(new ItemOverride(ArmorTrimItemFix.vanillaOverrideName(itemId, materialName).withPrefix("item/"), List.of(
                            new ItemOverride.Predicate(ArmorTrimItemFix.TRIM_PATTERN_PREDICATE_ID, DEFAULT_TRIM_VALUE), materialPredicate)));

                    ArmorTrimItemFix.TRIM_PATTERNS.forEach((pattern, value) ->
                            overrides.add(new ItemOverride(ArmorTrimItemFix.overrideName(itemId, pattern.getPath(), materialName).withPrefix("item/"), List.of(
                                    new ItemOverride.Predicate(ArmorTrimItemFix.TRIM_PATTERN_PREDICATE_ID, value), materialPredicate))));
                }
            }
        }

        return overrides;
    }

    public record MaterialData(String materialName, float propertyValue, ArmorMaterial armorMaterial,
                               String overrideName) implements Comparable<MaterialData> {

        public MaterialData(String materialName, float propertyValue) {
            this(materialName, propertyValue, null, null);
        }

        public String getName(Item item) {
            if (item instanceof ArmorItem armorItem && armorMaterial != null && overrideName != null && armorItem.getMaterial() == armorMaterial) {
                return overrideName;
            }
            return materialName;
        }

        @Override
        public int compareTo(@NotNull MaterialData o) {
            return Float.compare(propertyValue(), o.propertyValue());
        }
    }
}