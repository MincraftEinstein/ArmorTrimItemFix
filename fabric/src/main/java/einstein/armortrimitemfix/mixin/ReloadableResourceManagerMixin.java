package einstein.armortrimitemfix.mixin;

import einstein.armortrimitemfix.data.TrimMaterialReloadListener;
import einstein.armortrimitemfix.data.TrimPatternReloadListener;
import einstein.armortrimitemfix.data.TrimmableItemReloadListener;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerMixin {

    @Shadow
    public abstract void registerReloadListener(PreparableReloadListener listener);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void registerCustomReloadListeners(PackType type, CallbackInfo ci) {
        registerReloadListener(new TrimmableItemReloadListener());
        registerReloadListener(new TrimMaterialReloadListener());
        registerReloadListener(new TrimPatternReloadListener());
    }
}
