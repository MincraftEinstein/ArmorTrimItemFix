package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import einstein.armortrimitemfix.data.TrimDataReloadManager;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {

    // Hacky way to make sure armor trim data has loaded before the atlas.
    // Since atlas stitching does not wait for the Preparation Barrier
    // and takes less time to load than the armor trim data.
    // Meaning when the reload listeners are registered normally,
    // the armor trim data hasn't finished loading by the time it is necessary for the permutation injection.
    @WrapOperation(method = "loadAndStitch", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private <U> CompletableFuture<U> loadArmorTrimData(Supplier<U> supplier, Executor executor, Operation<CompletableFuture<U>> original, @Local(argsOnly = true) ResourceManager manager, @Local(argsOnly = true) Identifier atlasSprite) {
        if (!atlasSprite.equals(AtlasIds.ITEMS)) {
            return original.call(supplier, executor);
        }

        CompletableFuture<Void> materialsFuture = CompletableFuture.runAsync(() -> TrimDataReloadManager.loadMaterials(manager), executor);
        CompletableFuture<Void> patternsFuture = materialsFuture.thenRunAsync(() -> TrimDataReloadManager.loadPatterns(manager), executor);
        CompletableFuture<Void> itemsFuture = patternsFuture.thenRunAsync(() -> TrimDataReloadManager.loadItems(manager), executor);
        CompletableFuture<CompletableFuture<U>> originalFuture = itemsFuture.thenApplyAsync(v -> original.call(supplier, executor), executor);
        return originalFuture.join();
    }
}
