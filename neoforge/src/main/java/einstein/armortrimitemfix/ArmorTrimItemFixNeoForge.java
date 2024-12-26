package einstein.armortrimitemfix;

import einstein.armortrimitemfix.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(ArmorTrimItemFix.MOD_ID)
public class ArmorTrimItemFixNeoForge {

    public ArmorTrimItemFixNeoForge(IEventBus eventBus) {
        ArmorTrimItemFix.init();

        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                    ArmorTrimItemFix.registerDevCommand(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
        }
    }
}
