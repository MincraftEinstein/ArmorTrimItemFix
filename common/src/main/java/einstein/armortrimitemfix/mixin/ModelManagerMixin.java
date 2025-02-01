package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import einstein.armortrimitemfix.ArmorTrimItemFix;
import einstein.armortrimitemfix.data.ArmorTrimProperty;
import einstein.armortrimitemfix.data.TrimMaterialReloadListener;
import einstein.armortrimitemfix.data.TrimPatternReloadListener;
import einstein.armortrimitemfix.data.TrimmableItemReloadListener;
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

import static einstein.armortrimitemfix.ArmorTrimItemFix.addTexture;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @WrapOperation(method = "discoverModelDependencies", at = @At(value = "NEW", target = "(Ljava/util/Map;Lnet/minecraft/client/resources/model/UnbakedModel;)Lnet/minecraft/client/resources/model/ModelDiscovery;"))
    private static ModelDiscovery injectModels(Map<ResourceLocation, UnbakedModel> inputModels, UnbakedModel missingModel, Operation<ModelDiscovery> original, @Local(argsOnly = true) ClientItemInfoLoader.LoadedClientInfos clientInfos) {
        Map<ResourceLocation, UnbakedModel> map = new HashMap<>(inputModels);
        TrimmableItemReloadListener.TRIMMABLE_ITEMS.forEach((itemData) -> {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(itemData.item());
            Map<ResourceLocation, ClientItem> contents = clientInfos.contents();
            ItemModel.Unbaked fallbackModel = contents.remove(id).model();
            Map<String, ResourceLocation> textureLayers = itemData.layers();
            List<SelectItemModel.SwitchCase<ArmorTrimProperty.Data>> cases = new ArrayList<>();
            String equipmentName = itemData.type().getSerializedName();

            TrimPatternReloadListener.TRIM_PATTERNS.forEach(patternData -> {
                ResourceLocation patternId = patternData.pattern();
                String patternName = patternId.getPath();
                TrimMaterialReloadListener.TRIM_MATERIALS.forEach(materialData -> {
                    String materialName = materialData.getName(itemData.overrideMaterial().orElse(null));
                    ResourceLocation modelId = ArmorTrimItemFix.loc("item/" + id.getPath() + "_" + patternName + "_" + materialName + "_trim");
                    TextureSlots.Data.Builder builder = new TextureSlots.Data.Builder();

                    if (!textureLayers.containsKey("layer0")) {
                        addTexture(builder, 0, id.withPrefix("item/"));
                    }

                    int lastIndex = 0;
                    for (String layer : textureLayers.keySet()) {
                        int index = ItemModelGenerator.LAYERS.indexOf(layer);
                        addTexture(builder, index, textureLayers.get(layer));
                        lastIndex = index;
                    }

                    addTexture(builder, ++lastIndex, ArmorTrimItemFix.loc("trims/items/" + equipmentName + "/" + equipmentName + "_" + patternName + "_trim_" + materialName));
                    map.put(modelId, new BlockModel(ResourceLocation.withDefaultNamespace("item/generated"), List.of(), builder.build(), null, null, null));
                    cases.add(new SelectItemModel.SwitchCase<>(List.of(new ArmorTrimProperty.Data(patternId, materialData.materialId())), new BlockModelWrapper.Unbaked(modelId, itemData.tintSources())));
                });
            });

            contents.put(id, new ClientItem(new SelectItemModel.Unbaked(
                    new SelectItemModel.UnbakedSwitch<>(new ArmorTrimProperty(), cases),
                    Optional.of(fallbackModel)
            ), ClientItem.Properties.DEFAULT));
        });
        return original.call(map, missingModel);
    }
}
