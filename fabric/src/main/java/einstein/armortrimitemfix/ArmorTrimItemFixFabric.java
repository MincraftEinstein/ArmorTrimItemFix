package einstein.armortrimitemfix;

import einstein.armortrimitemfix.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;

public class ArmorTrimItemFixFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ArmorTrimItemFix.init();
        if (ModernFixWarningManager.IS_MODERNFIX_LOADED.get()) {
            ClientTickEvents.END_CLIENT_TICK.register(ModernFixWarningManager::clientTick);
        }

        if (Services.PLATFORM.isModLoaded(ArmorTrimItemFix.MORE_ARMOR_TRIMS_MOD_ID)) {
            ResourceLoader.registerBuiltinPack(ArmorTrimItemFix.MATS_PACK_PATH_ID.get(),
                    FabricLoader.getInstance().getModContainer(ArmorTrimItemFix.MOD_ID).orElseThrow(),
                    ArmorTrimItemFix.MATS_PACK_NAME, PackActivationType.ALWAYS_ENABLED
            );
        }

        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register(ArmorTrimItemFix::registerDevCommand);
        }
    }
}
