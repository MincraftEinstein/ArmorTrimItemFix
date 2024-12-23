package einstein.armortrimitemfix;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentModels;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
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
    public static final Map<Item, TrimmableData> TRIMMABLES = Util.make(new HashMap<>(), map -> {
        map.put(Items.LEATHER_HELMET, new TrimmableData("leather", ArmorType.HELMET));
        map.put(Items.LEATHER_CHESTPLATE, new TrimmableData("leather", ArmorType.CHESTPLATE));
        map.put(Items.LEATHER_LEGGINGS, new TrimmableData("leather", ArmorType.LEGGINGS));
        map.put(Items.LEATHER_BOOTS, new TrimmableData("leather", ArmorType.BOOTS));
        map.put(Items.CHAINMAIL_HELMET, new TrimmableData("chainmail", ArmorType.HELMET));
        map.put(Items.CHAINMAIL_CHESTPLATE, new TrimmableData("chainmail", ArmorType.CHESTPLATE));
        map.put(Items.CHAINMAIL_LEGGINGS, new TrimmableData("chainmail", ArmorType.LEGGINGS));
        map.put(Items.CHAINMAIL_BOOTS, new TrimmableData("chainmail", ArmorType.BOOTS));
        map.put(Items.IRON_HELMET, new TrimmableData("iron", ArmorType.HELMET));
        map.put(Items.IRON_CHESTPLATE, new TrimmableData("iron", ArmorType.CHESTPLATE));
        map.put(Items.IRON_LEGGINGS, new TrimmableData("iron", ArmorType.LEGGINGS));
        map.put(Items.IRON_BOOTS, new TrimmableData("iron", ArmorType.BOOTS));
        map.put(Items.GOLDEN_HELMET, new TrimmableData("golden", ArmorType.HELMET));
        map.put(Items.GOLDEN_CHESTPLATE, new TrimmableData("golden", ArmorType.CHESTPLATE));
        map.put(Items.GOLDEN_LEGGINGS, new TrimmableData("golden", ArmorType.LEGGINGS));
        map.put(Items.GOLDEN_BOOTS, new TrimmableData("golden", ArmorType.BOOTS));
        map.put(Items.DIAMOND_HELMET, new TrimmableData("diamond", ArmorType.HELMET));
        map.put(Items.DIAMOND_CHESTPLATE, new TrimmableData("diamond", ArmorType.CHESTPLATE));
        map.put(Items.DIAMOND_LEGGINGS, new TrimmableData("diamond", ArmorType.LEGGINGS));
        map.put(Items.DIAMOND_BOOTS, new TrimmableData("diamond", ArmorType.BOOTS));
        map.put(Items.NETHERITE_HELMET, new TrimmableData("netherite", ArmorType.HELMET));
        map.put(Items.NETHERITE_CHESTPLATE, new TrimmableData("netherite", ArmorType.CHESTPLATE));
        map.put(Items.NETHERITE_LEGGINGS, new TrimmableData("netherite", ArmorType.LEGGINGS));
        map.put(Items.NETHERITE_BOOTS, new TrimmableData("netherite", ArmorType.BOOTS));
        map.put(Items.TURTLE_HELMET, new TrimmableData("turtle", ArmorType.HELMET));
    });
    public static final TreeSet<MaterialData> TRIM_MATERIALS = Util.make(new TreeSet<>(), list -> {
        list.add(new MaterialData("quartz", 0.1F));
        list.add(new MaterialData("iron", 0.2F, EquipmentModels.IRON, "iron_darker"));
        list.add(new MaterialData("netherite", 0.3F, EquipmentModels.NETHERITE, "netherite_darker"));
        list.add(new MaterialData("redstone", 0.4F));
        list.add(new MaterialData("copper", 0.5F));
        list.add(new MaterialData("gold", 0.6F, EquipmentModels.GOLD, "gold_darker"));
        list.add(new MaterialData("emerald", 0.7F));
        list.add(new MaterialData("diamond", 0.8F, EquipmentModels.DIAMOND, "diamond_darker"));
        list.add(new MaterialData("lapis", 0.9F));
        list.add(new MaterialData("amethyst", 1.0F));
    });
    public static final Map<ResourceLocation, Float> TRIM_PATTERNS = createValueSortedMap(Util.make(new HashMap<>(), map -> {
        List<ResourceLocation> locations = Util.make(new ArrayList<>(), list -> {
            list.add(TrimPatterns.SENTRY.location());
            list.add(TrimPatterns.DUNE.location());
            list.add(TrimPatterns.COAST.location());
            list.add(TrimPatterns.WILD.location());
            list.add(TrimPatterns.WARD.location());
            list.add(TrimPatterns.EYE.location());
            list.add(TrimPatterns.VEX.location());
            list.add(TrimPatterns.TIDE.location());
            list.add(TrimPatterns.SNOUT.location());
            list.add(TrimPatterns.RIB.location());
            list.add(TrimPatterns.SPIRE.location());
            list.add(TrimPatterns.WAYFINDER.location());
            list.add(TrimPatterns.SHAPER.location());
            list.add(TrimPatterns.SILENCE.location());
            list.add(TrimPatterns.RAISER.location());
            list.add(TrimPatterns.HOST.location());
            list.add(TrimPatterns.FLOW.location());
            list.add(TrimPatterns.BOLT.location());
            list.add(moreArmorTrimsLoc("storm"));
            list.add(moreArmorTrimsLoc("ram"));
            list.add(moreArmorTrimsLoc("myth"));
            list.add(moreArmorTrimsLoc("greed"));
            list.add(moreArmorTrimsLoc("beast"));
            list.add(moreArmorTrimsLoc("fever"));
            list.add(moreArmorTrimsLoc("wraith"));
            list.add(moreArmorTrimsLoc("nihility"));
            list.add(moreArmorTrimsLoc("horizon"));
            list.add(moreArmorTrimsLoc("origin"));
        });
        float f = 0;
        float f1 = 1F / (locations.size() + 1);

        for (ResourceLocation location : locations) {
            map.put(location, f += f1);
        }
    }), Float::compare);

    public static void init() {
    }

    public static void clientSetup() {
        for (Item trimmable : ArmorTrimItemFix.TRIMMABLES.keySet()) {
            ArmorTrimItemFix.registerArmorTrimProperty(trimmable);
        }
    }

    public static void registerArmorTrimProperty(Item item) {
        ItemProperties.register(item, TRIM_PATTERN_PREDICATE_ID, (stack, level, entity, seed) -> {
            ArmorTrim trim = stack.get(DataComponents.TRIM);
            if (trim != null) {
                return TRIM_PATTERNS.getOrDefault(trim.pattern().unwrapKey().orElseThrow().location(), DEFAULT_TRIM_VALUE);
            }
            return 0;
        });
    }

    public static void registerDevCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("spawnArmorTrimItemFrames").executes(context -> {
            CommandSourceStack source = context.getSource();
            Player player = source.getPlayerOrException();
            Level level = player.level();
            RegistryAccess registryAccess = level.registryAccess();
            Registry<TrimMaterial> materialRegistry = registryAccess.lookupOrThrow(Registries.TRIM_MATERIAL);
            Registry<TrimPattern> patternRegistry = registryAccess.lookupOrThrow(Registries.TRIM_PATTERN);
            BlockPos playerPos = player.blockPosition();
            BlockPos.MutableBlockPos pos = playerPos.mutable();
            int[] zOffset = {pos.getZ()};

            TRIMMABLES.forEach((item, type) -> {
                if ((pos.getX() - playerPos.getX()) >= (materialRegistry.size() + 1) * 5) { // 5 is the number of rows
                    zOffset[0] = pos.getZ() + patternRegistry.size() + 1;
                    pos.setX(playerPos.getX());
                    pos.setZ(zOffset[0]);
                }

                materialRegistry.forEach(material -> {
                    pos.setX(pos.getX() + 1);
                    patternRegistry.forEach(pattern -> {
                        ItemFrame itemFrame = new ItemFrame(level, level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos.setZ(pos.getZ() + 1)), Direction.UP);
                        ItemStack stack = new ItemStack(item);
                        stack.set(DataComponents.TRIM, new ArmorTrim(materialRegistry.wrapAsHolder(material), patternRegistry.wrapAsHolder(pattern)));
                        itemFrame.setItem(stack);
                        level.addFreshEntity(itemFrame);
                    });
                    pos.setZ(zOffset[0]);
                });
                pos.setX(pos.getX() + 1);
            });

            source.sendSuccess(() -> Component.literal("Spawned item frames with armor trims"), true);
            return 1;
        }));
    }

    public static ResourceLocation overrideName(TrimmableData data, ResourceLocation item, String patternName, String materialName) {
        return loc("item/" + data.armorMaterial() + "/" + data.type().getName() + "/" + item.getPath() + "_" + patternName + "_" + materialName + "_trim");
    }

    public static ResourceLocation vanillaOverrideName(ResourceLocation item, String materialName) {
        return ResourceLocation.withDefaultNamespace(item.getPath() + "_" + materialName + "_trim");
    }

    public static ResourceLocation layerLoc(ArmorType armorType, String patternName, String materialName) {
        return loc("trims/items/" + armorType.getName() + "/" + armorType.getName() + "_" + patternName + "_trim_" + materialName);
    }

    public static boolean isDoubleLayered(Item item) {
        return item == Items.LEATHER_HELMET || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_BOOTS;
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private static ResourceLocation moreArmorTrimsLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath("more_armor_trims", path);
    }

    public static <K, V> Map<K, V> createValueSortedMap(Map<K, V> map, Comparator<V> comparator) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue(comparator))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public record MaterialData(String materialName, float propertyValue, ResourceLocation model,
                               String overrideName) implements Comparable<MaterialData> {

        public MaterialData(String materialName, float propertyValue) {
            this(materialName, propertyValue, null, null);
        }

        public String getName(Item item) {
            DataComponentMap components = item.components();

            if (components.has(DataComponents.EQUIPPABLE) && model != null && overrideName != null) {
                Optional<ResourceLocation> equipmentModel = components.get(DataComponents.EQUIPPABLE).model();

                if (equipmentModel.isPresent() && equipmentModel.get().equals(model)) {
                    return overrideName;
                }
            }
            return materialName;
        }

        @Override
        public int compareTo(@NotNull MaterialData o) {
            return Float.compare(propertyValue(), o.propertyValue());
        }
    }

    public record TrimmableData(String armorMaterial, ArmorType type) {

    }
}