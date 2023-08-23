package einstein.armortrimitemfix.mixin;

import com.mojang.datafixers.util.Either;
import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Mutable
    @Shadow
    @Final
    private Map<ResourceLocation, BlockModel> modelResources;

    @Shadow
    protected abstract void loadTopLevel(ModelResourceLocation modelResourceLocation);

    @Shadow
    public abstract UnbakedModel getModel(ResourceLocation resourceLocation);

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(BlockColors blockColors, ProfilerFiller filler, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStateResources, CallbackInfo ci) {
        Map<ResourceLocation, BlockModel> models = new HashMap<>(modelResources);
        Map<ResourceLocation, BlockModel> topLevelModels = new HashMap<>();
        ArmorTrimItemFix.TRIMMABLES.forEach((trimmable, prefix) -> {
            for (ArmorTrimItemFix.MaterialData data : ArmorTrimItemFix.TRIM_MATERIALS) {
                for (ResourceLocation pattern : ArmorTrimItemFix.TRIM_PATTERNS.keySet()) {
                    ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(trimmable);
                    ResourceLocation overrideName = ArmorTrimItemFix.overrideName(itemKey, pattern.getPath(), data.getName(trimmable));
                    ResourceLocation location = ModelBakery.MODEL_LISTER.idToFile(overrideName.withPrefix("item/"));

                    BlockModel model = new BlockModel(itemKey.withPrefix("item/"), List.of(),
                            Map.of(ArmorTrimItemFix.getLayer(trimmable), Either.left(new Material(InventoryMenu.BLOCK_ATLAS, ArmorTrimItemFix.layerLoc(prefix, pattern.getPath(), data.getName(trimmable))))),
                            null, null, ItemTransforms.NO_TRANSFORMS, List.of());
                    models.put(location, model);
                    this.modelResources = models;
                    topLevelModels.put(overrideName, model);
                }
            }
        });

        filler.popPush("armortrimitemfix_generated");
        topLevelModels.forEach((location, model) -> {
            loadTopLevel(new ModelResourceLocation(location, "inventory"));
            model.resolveParents(this::getModel);
        });
        filler.pop();
    }
}
