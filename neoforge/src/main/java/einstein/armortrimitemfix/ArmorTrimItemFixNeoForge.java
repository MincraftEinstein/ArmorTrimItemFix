package einstein.armortrimitemfix;

import einstein.armortrimitemfix.platform.Services;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(ArmorTrimItemFix.MOD_ID)
public class ArmorTrimItemFixNeoForge {

    public ArmorTrimItemFixNeoForge(IEventBus modEventBus) {
        ArmorTrimItemFix.init();
        if (Services.PLATFORM.isModLoaded(ArmorTrimItemFix.MORE_ARMOR_TRIMS_MOD_ID)) {
            modEventBus.addListener((AddPackFindersEvent event) ->
                    event.addPackFinders(ArmorTrimItemFix.MATS_PACK_LOCATION.get(), PackType.CLIENT_RESOURCES,
                            ArmorTrimItemFix.MATS_PACK_NAME, PackSource.BUILT_IN, true, Pack.Position.TOP));
        }

        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                    ArmorTrimItemFix.registerDevCommand(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection()));
        }
    }
}
