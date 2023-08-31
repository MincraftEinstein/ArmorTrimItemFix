package einstein.armortrimitemfix.compat;

import einstein.armortrimitemfix.api.TrimRegistry;

public class MoreArmorTrimsCompat extends AbstractTrimCompat {

    public MoreArmorTrimsCompat(String modId) {
        super(modId);
    }

    public void init(TrimRegistry registry) {
        registry.registerPattern(modLoc("beast"));
        registry.registerPattern(modLoc("fever"));
        registry.registerPattern(modLoc("greed"));
        registry.registerPattern(modLoc("myth"));
        registry.registerPattern(modLoc("ram"));
        registry.registerPattern(modLoc("storm"));
    }
}
