package einstein.armortrimitemfix.compat;

import einstein.armortrimitemfix.api.TrimRegistry;
import einstein.usefulslime.init.ModItems;

public class UsefulSlimeCompat extends AbstractTrimCompat {

    public UsefulSlimeCompat(String modId) {
        super(modId);
    }

    @Override
    public void init(TrimRegistry registry) {
        registry.registerTrimmableItem(ModItems.SLIME_HELMET.get());
        registry.registerTrimmableItem(ModItems.SLIME_CHESTPLATE.get());
        registry.registerTrimmableItem(ModItems.SLIME_LEGGINGS.get());
        registry.registerTrimmableItem(ModItems.SLIME_BOOTS.get());
    }
}
