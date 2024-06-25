package einstein.armortrimitemfix;

import einstein.armortrimitemfix.data.ModItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ArmorTrimItemFix.MOD_ID)
public class ArmorTrimItemFixForge {

    public ArmorTrimItemFixForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ArmorTrimItemFix.init();
        modEventBus.addListener((FMLClientSetupEvent event) -> ArmorTrimItemFix.clientSetup());
        modEventBus.addListener((GatherDataEvent event) -> {
            DataGenerator generator = event.getGenerator();
            generator.addProvider(event.includeClient(), new ModItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));
        });
    }
}
