package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import einstein.armortrimitemfix.data.ArmorTrimProperty;
import einstein.armortrimitemfix.data.EquipmentType;
import einstein.armortrimitemfix.data.TrimDataReloadManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.SpecialBlockModelWrapper;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

import static einstein.armortrimitemfix.ArmorTrimItemFix.*;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @WrapOperation(method = "discoverModelDependencies*", at = @At(value = "NEW", target = "(Ljava/util/Map;Lnet/minecraft/client/resources/model/UnbakedModel;)Lnet/minecraft/client/resources/model/ModelDiscovery;"))
    private static ModelDiscovery injectModels(Map<Identifier, UnbakedModel> unbakedModels, UnbakedModel missingUnbakedModel, Operation<ModelDiscovery> original, @Local(argsOnly = true) ClientItemInfoLoader.LoadedClientInfos clientInfos) {
        Map<Identifier, UnbakedModel> models = new HashMap<>(unbakedModels);
        Map<Identifier, ClientItem> contents = new HashMap<>(clientInfos.contents());

        TrimDataReloadManager.TRIMMABLE_ITEMS.forEach((itemData) -> {
            Identifier itemId = BuiltInRegistries.ITEM.getKey(itemData.item());
            ClientItem fallbackClientItem = contents.remove(itemId);
            ItemModel.Unbaked fallbackModel = fallbackClientItem != null ? fallbackClientItem.model() : null;
            Map<String, Identifier> textureLayers = itemData.layers();
            List<SelectItemModel.SwitchCase<ArmorTrimProperty.Data>> cases = new ArrayList<>();
            EquipmentType type = itemData.type();

            TrimDataReloadManager.TRIM_PATTERNS.forEach(patternId -> {
                String patternFileName = patternId.toDebugFileName();

                TrimDataReloadManager.TRIM_MATERIALS.forEach(materialData -> {
                    String materialFileName = materialData.getFileName(itemData.overrideId().orElse(null));
                    Identifier modelId = redirectedId(itemId.getNamespace(),
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

                    UnbakedModel model = new UnbakedModel() {
                        @Override
                        public Identifier parent() {
                            return GENERATED_MODEL;
                        }

                        @Override
                        public TextureSlots.Data textureSlots() {
                            return builder.build();
                        }
                    };

                    addTexture(builder, ++lastIndex, getTextureId(type, patternId).withSuffix("_" + materialFileName));
                    if (models.put(modelId, model) != null) {
                        LOGGER.warn("Duplicate model found with id: [{}]. Overriding existing model", modelId);
                    }

                    cases.add(new SelectItemModel.SwitchCase<>(
                            List.of(new ArmorTrimProperty.Data(patternId, materialData.materialId())),
                            new CuboidItemModelWrapper.Unbaked(modelId, Optional.empty(), itemData.tintSources())
                    ));
                });
            });

            contents.put(itemId, new ClientItem(new SelectItemModel.Unbaked(
                    Optional.empty(),
                    new SelectItemModel.UnbakedSwitch<>(new ArmorTrimProperty(), cases),
                    Optional.ofNullable(fallbackModel)
            ), ClientItem.Properties.DEFAULT));
        });

        ((LoadedClientInfosAccessor) (Object) clientInfos).setContents(contents);
        return original.call(models, missingUnbakedModel);
    }
}
