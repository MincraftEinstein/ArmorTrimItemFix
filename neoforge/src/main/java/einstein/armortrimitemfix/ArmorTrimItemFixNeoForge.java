package einstein.armortrimitemfix;

import einstein.armortrimitemfix.data.ModItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(ArmorTrimItemFix.MOD_ID)
public class ArmorTrimItemFixNeoForge {

    public ArmorTrimItemFixNeoForge(IEventBus eventBus) {
        ArmorTrimItemFix.init();
        eventBus.addListener(this::clientSetup);
        eventBus.addListener((GatherDataEvent event) -> {
            DataGenerator generator = event.getGenerator();
            generator.addProvider(event.includeClient(), new ModItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));
        });
    }

    void clientSetup(FMLClientSetupEvent event) {
        ArmorTrimItemFix.clientSetup();
    }
}
