package einstein.armortrimitemfix;

import einstein.armortrimitemfix.compat.MoreArmorTrimsCompat;
import net.fabricmc.api.ClientModInitializer;
import net.masik.morearmortrims.MoreArmorTrims;

import static einstein.armortrimitemfix.ArmorTrimItemFix.clientSetup;
import static einstein.armortrimitemfix.ArmorTrimItemFix.registerCompat;

public class ArmorTrimItemFixClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerCompat(MoreArmorTrims.MOD_ID, MoreArmorTrimsCompat::new);
        clientSetup();
    }
}
