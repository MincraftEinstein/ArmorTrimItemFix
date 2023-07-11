package einstein.armortrimitemfix;

import net.minecraft.Util;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ArmorTrimItemFix {

    public static final String MOD_ID = "armortrimitemfix";
    public static final String MOD_NAME = "ArmorTrimItemFix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final ResourceLocation PREDICATE_ID = loc("trim_pattern");
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
    public static final Map<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = Util.make(new HashMap<>(), map -> {
        map.put(TrimMaterials.QUARTZ, 0.1F);
        map.put(TrimMaterials.IRON, 0.2F);
        map.put(TrimMaterials.NETHERITE, 0.3F);
        map.put(TrimMaterials.REDSTONE, 0.4F);
        map.put(TrimMaterials.COPPER, 0.5F);
        map.put(TrimMaterials.GOLD, 0.6F);
        map.put(TrimMaterials.EMERALD, 0.7F);
        map.put(TrimMaterials.DIAMOND, 0.8F);
        map.put(TrimMaterials.LAPIS, 0.9F);
        map.put(TrimMaterials.AMETHYST, 1F);
    });
    public static final Map<ResourceLocation, Float> TRIM_PATTERNS = Util.make(new HashMap<>(), map -> {
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
    });

    public static void init() {
    }

    public static void clientSetup() {
        for (Item trimmable : ArmorTrimItemFix.TRIMMABLES.keySet()) {
            ArmorTrimItemFix.registerArmorTrimProperty(trimmable);
        }
    }

    public static void registerArmorTrimProperty(Item item) {
        ItemProperties.register(item, PREDICATE_ID, (stack, level, entity, seed) -> {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(ArmorTrim.TAG_TRIM_ID)) {
                CompoundTag trimTag = tag.getCompound(ArmorTrim.TAG_TRIM_ID);
                if (trimTag.contains("pattern")) {
                    String pattern = trimTag.getString("pattern");
                    return TRIM_PATTERNS.get(ResourceLocation.tryParse(pattern));
                }
            }
            return 0;
        });
    }

    public static ResourceLocation overrideName(ResourceLocation item, String patternName, String materialName) {
        return loc(item.getPath() + "_" + patternName + "_" + materialName + "_trim");
    }

    public static ResourceLocation layerLoc(ArmorItem.Type armorType, String patternName, String materialName) {
        return loc("trims/items/" + armorType.getName() + "_" + patternName + "_trim_" + materialName);
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}