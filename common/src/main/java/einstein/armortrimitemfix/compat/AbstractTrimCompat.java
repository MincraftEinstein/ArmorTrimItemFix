package einstein.armortrimitemfix.compat;

import einstein.armortrimitemfix.api.TrimRegistry;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractTrimCompat {

    private final String modId;

    public AbstractTrimCompat(String modId) {
        this.modId = modId;
    }

    public abstract void init(TrimRegistry registry);

    protected ResourceLocation modLoc(String path) {
        return new ResourceLocation(modId, path);
    }
}
