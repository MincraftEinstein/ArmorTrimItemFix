package einstein.armortrimitemfix;

import com.google.common.base.Suppliers;
import com.mojang.brigadier.CommandDispatcher;
import einstein.armortrimitemfix.data.EquipmentType;
import einstein.armortrimitemfix.data.TrimmableItemReloadListener;
import einstein.armortrimitemfix.platform.Services;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
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
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

public class ArmorTrimItemFix {

    public static final String MOD_ID = "armortrimitemfix";
    public static final String MOD_NAME = "ArmorTrimItemFix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final String PALETTES_DIRECTORY = "trims/color_palettes/";
    public static final ResourceLocation PALETTE_KEY = ResourceLocation.withDefaultNamespace(PALETTES_DIRECTORY + "trim_palette");
    public static final ResourceLocation BLOCKS_ATLAS = ResourceLocation.withDefaultNamespace("blocks");
    public static final ResourceLocation GENERATED_MODEL = ResourceLocation.withDefaultNamespace("item/generated");
    public static final Supplier<ResourceLocation> MATS_PACK_LOCATION = Suppliers.memoize(() -> loc("more_armor_trims_support").withPrefix(Services.PLATFORM.getPlatformName().equals("NeoForge") ? "resourcepacks/" : ""));
    public static final Component MATS_PACK_NAME = Component.translatable("resourcePack.armortrimitemfix.more_armor_trims_support.name");
    public static final String MORE_ARMOR_TRIMS_MOD_ID = "more_armor_trims";
    private static final float EXPAND_AMOUNT = 0.001F;

    public static void init() {
        if (ModernFixWarningManager.IS_MODERNFIX_LOADED.get()) {
            ModernFixWarningManager.load();
        }
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

            // I know this is bad code since the list is loaded from a resource pack, but it works,
            // so I don't care, because this is a development command
            TrimmableItemReloadListener.TRIMMABLE_ITEMS.forEach(data -> {
                if ((pos.getX() - playerPos.getX()) >= (materialRegistry.size() + 1) * 5) { // 5 is the number of rows
                    zOffset[0] = pos.getZ() + patternRegistry.size() + 1;
                    pos.setX(playerPos.getX());
                    pos.setZ(zOffset[0]);
                }

                Item item = data.item();
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

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation redirectedLoc(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace.equals(ResourceLocation.DEFAULT_NAMESPACE) ? MOD_ID : namespace, path);
    }

    public static FileToIdConverter createLister(String directory) {
        return FileToIdConverter.json(MOD_ID + "/" + directory);
    }

    @SuppressWarnings("deprecation")
    public static void addTexture(TextureSlots.Data.Builder builder, int index, ResourceLocation textureLayers) {
        builder.addTexture("layer" + index, new Material(TextureAtlas.LOCATION_BLOCKS, textureLayers));
    }

    public static ResourceLocation getTextureLocation(EquipmentType type, ResourceLocation patternId) {
        String typeName = type.getSerializedName();
        return redirectedLoc(patternId.getNamespace(), "trims/items/" + typeName + "/" + typeName + "_" + patternId.getPath() + "_trim");
    }

    public static void addColorPalette(Map<String, ResourceLocation> permutations, ResourceLocation materialId) {
        permutations.put(materialId.toDebugFileName(), materialId.withPrefix(PALETTES_DIRECTORY));
    }

    public static Vector3fc expand(Vector3fc vertex, int layerIndex, boolean invert) {
        float amount = layerIndex * EXPAND_AMOUNT;
        if (amount > 0) {
            return new Vector3f(
                    add(vertex.x(), amount, invert),
                    add(vertex.y(), amount, invert),
                    add(vertex.z(), amount, invert)
            );
        }
        return vertex;
    }

    private static float add(float value, float amount, boolean invert) {
        if (invert) {
            return value - amount;
        }
        return value + amount;
    }
}