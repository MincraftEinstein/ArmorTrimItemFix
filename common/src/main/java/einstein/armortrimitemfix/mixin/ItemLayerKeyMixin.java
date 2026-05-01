package einstein.armortrimitemfix.mixin;

import einstein.armortrimitemfix.ItemLayerKeyContext;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.resources.model.cuboid.ItemModelGenerator$ItemLayerKey")
public class ItemLayerKeyMixin {
    @Shadow
    @Final
    private int layerIndex;

    @Inject(
            method = "compute(Lnet/minecraft/client/resources/model/ModelBaker;)Lnet/minecraft/client/resources/model/geometry/QuadCollection;",
            at = @At("HEAD")
    )
    void captureLayerIndex(ModelBaker modelBakery, CallbackInfoReturnable<QuadCollection> cir) {
        ItemLayerKeyContext.set(this.layerIndex);
    }

    @Inject(
            method = "compute(Lnet/minecraft/client/resources/model/ModelBaker;)Lnet/minecraft/client/resources/model/geometry/QuadCollection;",
            at = @At("RETURN")
    )
    void clearLayerIndex(ModelBaker modelBakery, CallbackInfoReturnable<QuadCollection> cir) {
        ItemLayerKeyContext.remove();
    }
}
