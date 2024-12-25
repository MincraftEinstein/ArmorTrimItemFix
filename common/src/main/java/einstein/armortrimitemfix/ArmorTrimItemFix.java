package einstein.armortrimitemfix;

import com.mojang.brigadier.CommandDispatcher;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.Util;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ArmorTrimItemFix {

    public static final String MOD_ID = "armortrimitemfix";
    public static final String MOD_NAME = "ArmorTrimItemFix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final ResourceLocation PALETTE_KEY = ResourceLocation.withDefaultNamespace("trims/color_palettes/trim_palette");
    public static final ResourceLocation BLOCKS_ATLAS = ResourceLocation.withDefaultNamespace("blocks");
    public static final Map<Item, TrimmableData> TRIMMABLES = Util.make(new HashMap<>(), map -> {
        map.put(Items.LEATHER_HELMET, new TrimmableData("leather", ArmorType.HELMET, EquipmentAssets.LEATHER,
                Util.make(new Int2ObjectArrayMap<>(), layerMap ->
                        layerMap.put(1, ResourceLocation.withDefaultNamespace("item/leather_helmet_overlay"))), List.of(new Dye(-6265536))));
        map.put(Items.LEATHER_CHESTPLATE, new TrimmableData("leather", ArmorType.CHESTPLATE, EquipmentAssets.LEATHER,
                Util.make(new Int2ObjectArrayMap<>(), layerMap ->
                        layerMap.put(1, ResourceLocation.withDefaultNamespace("item/leather_chestplate_overlay"))), List.of(new Dye(-6265536))));
        map.put(Items.LEATHER_LEGGINGS, new TrimmableData("leather", ArmorType.LEGGINGS, EquipmentAssets.LEATHER,
                Util.make(new Int2ObjectArrayMap<>(), layerMap ->
                        layerMap.put(1, ResourceLocation.withDefaultNamespace("item/leather_leggings_overlay"))), List.of(new Dye(-6265536))));
        map.put(Items.LEATHER_BOOTS, new TrimmableData("leather", ArmorType.BOOTS, EquipmentAssets.LEATHER,
                Util.make(new Int2ObjectArrayMap<>(), layerMap ->
                        layerMap.put(1, ResourceLocation.withDefaultNamespace("item/leather_boots_overlay"))), List.of(new Dye(-6265536))));
//        map.put(Items.CHAINMAIL_HELMET, new TrimmableData("chainmail", ArmorType.HELMET));
//        map.put(Items.CHAINMAIL_CHESTPLATE, new TrimmableData("chainmail", ArmorType.CHESTPLATE));
//        map.put(Items.CHAINMAIL_LEGGINGS, new TrimmableData("chainmail", ArmorType.LEGGINGS));
//        map.put(Items.CHAINMAIL_BOOTS, new TrimmableData("chainmail", ArmorType.BOOTS));
//        map.put(Items.IRON_HELMET, new TrimmableData("iron", ArmorType.HELMET));
        map.put(Items.IRON_CHESTPLATE, new TrimmableData("iron", ArmorType.CHESTPLATE, EquipmentAssets.IRON));
//        map.put(Items.IRON_LEGGINGS, new TrimmableData("iron", ArmorType.LEGGINGS));
//        map.put(Items.IRON_BOOTS, new TrimmableData("iron", ArmorType.BOOTS));
//        map.put(Items.GOLDEN_HELMET, new TrimmableData("golden", ArmorType.HELMET));
//        map.put(Items.GOLDEN_CHESTPLATE, new TrimmableData("golden", ArmorType.CHESTPLATE));
//        map.put(Items.GOLDEN_LEGGINGS, new TrimmableData("golden", ArmorType.LEGGINGS));
//        map.put(Items.GOLDEN_BOOTS, new TrimmableData("golden", ArmorType.BOOTS));
//        map.put(Items.DIAMOND_HELMET, new TrimmableData("diamond", ArmorType.HELMET));
        map.put(Items.DIAMOND_CHESTPLATE, new TrimmableData("diamond", ArmorType.CHESTPLATE, EquipmentAssets.DIAMOND));
//        map.put(Items.DIAMOND_LEGGINGS, new TrimmableData("diamond", ArmorType.LEGGINGS));
//        map.put(Items.DIAMOND_BOOTS, new TrimmableData("diamond", ArmorType.BOOTS));
//        map.put(Items.NETHERITE_HELMET, new TrimmableData("netherite", ArmorType.HELMET));
//        map.put(Items.NETHERITE_CHESTPLATE, new TrimmableData("netherite", ArmorType.CHESTPLATE));
//        map.put(Items.NETHERITE_LEGGINGS, new TrimmableData("netherite", ArmorType.LEGGINGS));
//        map.put(Items.NETHERITE_BOOTS, new TrimmableData("netherite", ArmorType.BOOTS));
//        map.put(Items.TURTLE_HELMET, new TrimmableData("turtle", ArmorType.HELMET));
    });
    public static final List<ResourceKey<TrimPattern>> TRIM_PATTERNS = Util.make(new ArrayList<>(), list -> {
        list.add(TrimPatterns.SILENCE);
    });
    public static final List<MaterialData> TRIM_MATERIALS = Util.make(new ArrayList<>(), list -> {
        list.add(new MaterialData(TrimMaterials.DIAMOND, Map.of(EquipmentAssets.DIAMOND, "diamond_darker")));
    });
    public static final List<ArmorType> ARMOR_TYPES = Util.make(new ArrayList<>(), list -> {
        list.add(ArmorType.HELMET);
        list.add(ArmorType.CHESTPLATE);
        list.add(ArmorType.LEGGINGS);
        list.add(ArmorType.BOOTS);
    });

    public static void init() {
    }

    public static void clientSetup() {
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

    public record MaterialData(ResourceKey<TrimMaterial> key,
                               Map<ResourceKey<EquipmentAsset>, String> overrides) {

        public MaterialData(ResourceKey<TrimMaterial> key) {
            this(key, Map.of());
        }

        public String getName(ResourceKey<EquipmentAsset> equipmentAsset) {
            if (equipmentAsset != null) {
                String overrideName = overrides.get(equipmentAsset);

                if (overrideName != null) {
                    return overrideName;
                }
            }
            return key.location().getPath();
        }
    }

    public record TrimmableData(String armorMaterial, ArmorType type,
                                @Nullable ResourceKey<EquipmentAsset> equipmentAsset,
                                Int2ObjectMap<ResourceLocation> layersTextures,
                                List<ItemTintSource> tintSources) {

        public TrimmableData(String armorMaterial, ArmorType type, ResourceKey<EquipmentAsset> equipmentAsset) {
            this(armorMaterial, type, equipmentAsset, new Int2ObjectArrayMap<>(), List.of());
        }
    }
}