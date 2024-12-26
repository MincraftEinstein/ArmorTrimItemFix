package einstein.armortrimitemfix;

import com.mojang.brigadier.CommandDispatcher;
import einstein.armortrimitemfix.data.EquipmentType;
import einstein.armortrimitemfix.data.TrimmableItemReloadListener;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArmorTrimItemFix {

    public static final String MOD_ID = "armortrimitemfix";
    public static final String MOD_NAME = "ArmorTrimItemFix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final String PALETTES_DIRECTORY = "trims/color_palettes/";
    public static final ResourceLocation PALETTE_KEY = ResourceLocation.withDefaultNamespace(PALETTES_DIRECTORY + "trim_palette");
    public static final ResourceLocation BLOCKS_ATLAS = ResourceLocation.withDefaultNamespace("blocks");

    public static void init() {
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

    public static FileToIdConverter createLister(String directory) {
        return FileToIdConverter.json(MOD_ID + "/" + directory);
    }
}