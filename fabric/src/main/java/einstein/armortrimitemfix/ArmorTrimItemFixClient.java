package einstein.armortrimitemfix;

import net.fabricmc.api.ClientModInitializer;

public class ArmorTrimItemFixClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ArmorTrimItemFix.clientSetup();
    }
}
