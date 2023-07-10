package einstein.armortrimitemfix;

import com.mojang.logging.LogUtils;
import einstein.armortrimitemfix.data.ModItemModelProvider;
import net.minecraft.Util;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.DataGenerator;
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
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(ArmorTrimItemFix.MOD_ID)
public class ArmorTrimItemFix {

    public static final String MOD_ID = "armortrimitemfix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation PREDICATE_ID = loc("trim_pattern");
    public static final Map<Item, ArmorItem.Type> TRIMMABLES = Util.make(new HashMap<>(), map -> {
        map.put(Items.IRON_HELMET, ArmorItem.Type.HELMET);
        map.put(Items.IRON_CHESTPLATE, ArmorItem.Type.CHESTPLATE);
        map.put(Items.IRON_LEGGINGS, ArmorItem.Type.LEGGINGS);
        map.put(Items.IRON_BOOTS, ArmorItem.Type.BOOTS);
    });
    public static final Map<ResourceKey<TrimMaterial>, Float> TRIM_MATERIALS = Util.make(new HashMap<>(), map -> {
        map.put(TrimMaterials.AMETHYST, 1F);
    });
    public static final Map<ResourceLocation, Float> TRIM_PATTERNS = Util.make(new HashMap<>(), map -> {
        map.put(TrimPatterns.SILENCE.location(), 0.0001F);
    });

    public ArmorTrimItemFix() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::generateData);
    }

    void generateData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));
    }

    void clientSetup(FMLClientSetupEvent event) {
        for (Item trimmable : TRIMMABLES.keySet()) {
            registerArmorTrimProperty(trimmable);
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

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
