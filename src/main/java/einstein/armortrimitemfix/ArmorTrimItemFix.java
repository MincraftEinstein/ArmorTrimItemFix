package einstein.armortrimitemfix;

import com.mojang.logging.LogUtils;
import einstein.armortrimitemfix.data.ModItemModelProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ArmorTrimItemFix.MOD_ID)
public class ArmorTrimItemFix {

    public static final String MOD_ID = "armortrimitemfix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ResourceLocation PREDICATE_ID = loc("trim_pattern");

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
        registerArmorTrimProperty(Items.IRON_HELMET);
        registerArmorTrimProperty(Items.IRON_CHESTPLATE);
        registerArmorTrimProperty(Items.IRON_LEGGINGS);
        registerArmorTrimProperty(Items.IRON_BOOTS);
    }

    public static void registerArmorTrimProperty(Item item) {
        ModItemModelProvider.TRIMMABLES.add(item);
        ItemProperties.register(item, PREDICATE_ID, (stack, level, entity, seed) -> {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(ArmorTrim.TAG_TRIM_ID)) {
                CompoundTag trimTag = tag.getCompound(ArmorTrim.TAG_TRIM_ID);
                if (trimTag.contains("pattern")) {
                    String pattern = trimTag.getString("pattern");
                    if (pattern.equals("minecraft:silence")) {
                        return 1;
                    }
                }
            }
            return 0;
        });
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
