package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import einstein.armortrimitemfix.ArmorTrimItemFix;
import einstein.armortrimitemfix.data.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @SuppressWarnings("deprecation")
    @WrapOperation(method = "discoverModelDependencies", at = @At(value = "NEW", target = "(Ljava/util/Map;Lnet/minecraft/client/resources/model/UnbakedModel;)Lnet/minecraft/client/resources/model/ModelDiscovery;"))
    private static ModelDiscovery injectModels(Map<ResourceLocation, UnbakedModel> inputModels, UnbakedModel missingModel, Operation<ModelDiscovery> original, @Local(argsOnly = true) ClientItemInfoLoader.LoadedClientInfos clientInfos) {
        Map<ResourceLocation, UnbakedModel> map = new HashMap<>(inputModels);
        TrimmableItemReloadListener.TRIMMABLE_ITEMS.forEach((itemData) -> {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(itemData.item());
            Map<ResourceLocation, ClientItem> contents = clientInfos.contents();
            ItemModel.Unbaked fallbackModel = contents.remove(id).model();
            List<TrimmableItemData.TextureLayer> layersTextures = itemData.layers();
            List<SelectItemModel.SwitchCase<ArmorTrimProperty.Data>> cases = new ArrayList<>();
            String equipmentName = itemData.type().getSerializedName();

            TrimPatternReloadListener.TRIM_PATTERNS.forEach(patternData -> {
                ResourceLocation patternId = patternData.pattern();
                String patternName = patternId.getPath();
                TrimMaterialReloadListener.TRIM_MATERIALS.forEach(materialData -> {
                    String materialName = materialData.getName(itemData.overrideMaterial().orElse(null));
                    ResourceLocation modelId = ArmorTrimItemFix.loc("item/" + id.getPath() + "_" + patternName + "_" + materialName + "_trim");
                    TextureSlots.Data.Builder builder = new TextureSlots.Data.Builder();

                    final int[] i = {0};
                    if (layersTextures.stream().noneMatch(layer -> layer.index() == 0)) {
                        builder.addTexture("layer0", new Material(TextureAtlas.LOCATION_BLOCKS, id.withPrefix("item/")));
                    }

                    layersTextures.forEach(layer -> {
                        int index = layer.index();
                        builder.addTexture("layer" + index, new Material(TextureAtlas.LOCATION_BLOCKS, layer.texture()));
                        i[0] = index;
                    });

                    i[0]++;
                    builder.addTexture("layer" + i[0], new Material(TextureAtlas.LOCATION_BLOCKS, ArmorTrimItemFix.loc("trims/items/" + equipmentName + "/" + equipmentName + "_" + patternName + "_trim_" + materialName)));
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
