package einstein.armortrimitemfix;

import einstein.armortrimitemfix.data.ModItemModelProvider;
import einstein.armortrimitemfix.platform.Services;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(ArmorTrimItemFix.MOD_ID)
public class ArmorTrimItemFixNeoForge {

    public ArmorTrimItemFixNeoForge(IEventBus eventBus) {
        ArmorTrimItemFix.init();
        eventBus.addListener((FMLClientSetupEvent event) -> ArmorTrimItemFix.clientSetup());
        eventBus.addListener((GatherDataEvent event) -> {
            DataGenerator generator = event.getGenerator();
            generator.addProvider(event.includeClient(), new ModItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));
        });

        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                    ArmorTrimItemFix.registerDevCommand(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
        }
    }
}
