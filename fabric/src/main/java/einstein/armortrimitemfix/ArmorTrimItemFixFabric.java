package einstein.armortrimitemfix;

import einstein.armortrimitemfix.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ArmorTrimItemFixFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ArmorTrimItemFix.init();

        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register(ArmorTrimItemFix::registerDevCommand);
        }
    }
}
