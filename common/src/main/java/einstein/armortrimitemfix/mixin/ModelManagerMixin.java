package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import einstein.armortrimitemfix.data.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

import static einstein.armortrimitemfix.ArmorTrimItemFix.*;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @WrapOperation(method = "discoverModelDependencies", at = @At(value = "NEW", target = "(Ljava/util/Map;Lnet/minecraft/client/resources/model/UnbakedModel;)Lnet/minecraft/client/resources/model/ModelDiscovery;"))
    private static ModelDiscovery injectModels(Map<ResourceLocation, UnbakedModel> originalModels, UnbakedModel missingModel, Operation<ModelDiscovery> original, @Local(argsOnly = true) ClientItemInfoLoader.LoadedClientInfos clientInfos) {
        Map<ResourceLocation, UnbakedModel> models = new HashMap<>(originalModels);
        Map<ResourceLocation, ClientItem> contents = new HashMap<>(clientInfos.contents());

        TrimmableItemReloadListener.TRIMMABLE_ITEMS.forEach((itemData) -> {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemData.item());
            ClientItem fallbackClientItem = contents.remove(itemId);
            ItemModel.Unbaked fallbackModel = fallbackClientItem != null ? fallbackClientItem.model() : null;
            Map<String, ResourceLocation> textureLayers = itemData.layers();
            List<SelectItemModel.SwitchCase<ArmorTrimProperty.Data>> cases = new ArrayList<>();
            EquipmentType type = itemData.type();

            TrimPatternReloadListener.TRIM_PATTERNS.forEach(patternData -> {
                ResourceLocation patternId = patternData.pattern();
                String patternFileName = patternId.toDebugFileName();

                TrimMaterialReloadListener.TRIM_MATERIALS.forEach(materialData -> {
                    String materialFileName = materialData.getFileName(itemData.overrideId().orElse(null));
                    ResourceLocation modelId = redirectedLoc(itemId.getNamespace(),
                            "item/" + itemId.getPath() + "-" + patternFileName + "-" + materialFileName + "-trim");
                    TextureSlots.Data.Builder builder = new TextureSlots.Data.Builder();

                    if (!textureLayers.containsKey("layer0")) {
                        addTexture(builder, 0, itemId.withPrefix("item/"));
                    }

                    int lastIndex = 0;
                    for (String layer : textureLayers.keySet()) {
                        int index = ItemModelGenerator.LAYERS.indexOf(layer);
                        addTexture(builder, index, textureLayers.get(layer));
                        lastIndex = index;
                    }

                    addTexture(builder, ++lastIndex, getTextureLocation(type, patternId).withSuffix("_" + materialFileName));
                    if (models.put(modelId, new BlockModel(GENERATED_MODEL, List.of(), builder.build(), null, null, null)) != null) {
                        LOGGER.warn("Duplicate model found with id: [{}]. Overriding existing model", modelId);
                    }

                    cases.add(new SelectItemModel.SwitchCase<>(
                            List.of(new ArmorTrimProperty.Data(patternId, materialData.materialId())),
                            new BlockModelWrapper.Unbaked(modelId, itemData.tintSources())
                    ));
                });
            });

            contents.put(itemId, new ClientItem(new SelectItemModel.Unbaked(
                    new SelectItemModel.UnbakedSwitch<>(new ArmorTrimProperty(), cases),
                    Optional.ofNullable(fallbackModel)
            ), ClientItem.Properties.DEFAULT));
        });

        ((LoadedClientInfosAccessor) (Object) clientInfos).setContents(contents);
        return original.call(models, missingModel);
    }
}
