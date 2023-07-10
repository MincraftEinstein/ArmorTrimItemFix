package einstein.armortrimitemfix;

import einstein.armortrimitemfix.data.ModItemModelProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ArmorTrimItemFix.MOD_ID)
public class ArmorTrimItemFixForge {

    public ArmorTrimItemFixForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::generateData);
    }

    void generateData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), new ModItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));
    }

    void clientSetup(FMLClientSetupEvent event) {
        for (Item trimmable : ArmorTrimItemFix.TRIMMABLES.keySet()) {
            registerArmorTrimProperty(trimmable);
        }
    }

    public static void registerArmorTrimProperty(Item item) {
        ItemProperties.register(item, ArmorTrimItemFix.PREDICATE_ID, (stack, level, entity, seed) -> {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(ArmorTrim.TAG_TRIM_ID)) {
                CompoundTag trimTag = tag.getCompound(ArmorTrim.TAG_TRIM_ID);
                if (trimTag.contains("pattern")) {
                    String pattern = trimTag.getString("pattern");
                    return ArmorTrimItemFix.TRIM_PATTERNS.get(ResourceLocation.tryParse(pattern));
                }
            }
            return 0;
        });
    }
}
