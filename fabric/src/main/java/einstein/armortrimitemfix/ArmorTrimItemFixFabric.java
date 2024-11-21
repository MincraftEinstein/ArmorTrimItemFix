package einstein.armortrimitemfix;

import einstein.armortrimitemfix.data.ModModelProvider;
import einstein.armortrimitemfix.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.client.renderer.item.ItemProperties;

public class ArmorTrimItemFixFabric implements ModInitializer, ClientModInitializer, DataGeneratorEntrypoint {

    @Override
    public void onInitialize() {
        ArmorTrimItemFix.init();

        if (Services.PLATFORM.isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register(ArmorTrimItemFix::registerDevCommand);
        }
    }

    @Override
    public void onInitializeClient() {
        ArmorTrimItemFix.clientSetup();
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.createPack().addProvider(ModModelProvider::new);
    }
}
