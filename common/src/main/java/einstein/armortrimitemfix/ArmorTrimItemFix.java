package einstein.armortrimitemfix;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
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
        float f1 = 1 / 19F; // 19 is the total number of trims + 1
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
        map.put(TrimPatterns.FLOW.location(), f += f1);
        map.put(TrimPatterns.BOLT.location(), f += f1);
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
            Registry<TrimMaterial> materialRegistry = registryAccess.registryOrThrow(Registries.TRIM_MATERIAL);
            Registry<TrimPattern> patternRegistry = registryAccess.registryOrThrow(Registries.TRIM_PATTERN);
            BlockPos playerPos = player.blockPosition();
            BlockPos.MutableBlockPos pos = playerPos.mutable();
            int[] zOffset = {pos.getZ()};

            TRIMMABLES.forEach((item, type) -> {
                if ((pos.getX() - playerPos.getX()) > (materialRegistry.size() + 1) * 4) {
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

    public static ResourceLocation overrideName(ResourceLocation item, String patternName, String materialName) {
        return loc(item.getPath() + "_" + patternName + "_" + materialName + "_trim");
    }

    public static ResourceLocation vanillaOverrideName(ResourceLocation item, String materialName) {
        return ResourceLocation.withDefaultNamespace(item.getPath() + "_" + materialName + "_trim");
    }

    public static ResourceLocation layerLoc(ArmorItem.Type armorType, String patternName, String materialName) {
        return loc("trims/items/" + armorType.getName() + "_" + patternName + "_trim_" + materialName);
    }

    public static boolean isDoubleLayered(Item item) {
        return item == Items.LEATHER_HELMET || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_BOOTS;
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static <K, V> Map<K, V> createValueSortedMap(Map<K, V> map, Comparator<V> comparator) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue(comparator))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public record MaterialData(String materialName, float propertyValue, Holder<ArmorMaterial> armorMaterial,
                               String overrideName) implements Comparable<MaterialData> {

        public MaterialData(String materialName, float propertyValue) {
            this(materialName, propertyValue, null, null);
        }

        public String getName(Item item) {
            if (item instanceof ArmorItem armorItem && armorMaterial != null && overrideName != null && armorItem.getMaterial().is(armorMaterial.unwrapKey().orElseThrow())) {
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