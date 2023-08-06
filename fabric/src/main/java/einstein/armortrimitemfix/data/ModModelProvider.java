package einstein.armortrimitemfix.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        ArmorTrimItemFix.TRIMMABLES.forEach((trimmable, armorType) -> {
            ResourceLocation trimmableKey = BuiltInRegistries.ITEM.getKey(trimmable).withPrefix("item/");
            if (trimmableKey != null) {
                JsonArray overrides = new JsonArray();

                for (ArmorTrimItemFix.MaterialData materialData : ArmorTrimItemFix.TRIM_MATERIALS) {
                    float materialValue = materialData.propertyValue();
                    String materialName = materialData.getName(trimmable);

                    overrides.add(createOverride(ArmorTrimItemFix.vanillaOverrideName(trimmableKey, materialName).toString(), ArmorTrimItemFix.DEFAULT_TRIM_VALUE, materialValue));

                    ArmorTrimItemFix.TRIM_PATTERNS.forEach((pattern, patternValue) -> {
                        String patternName = pattern.getPath();
                        ResourceLocation overrideName = ArmorTrimItemFix.overrideName(trimmableKey, patternName, materialName);
                        ResourceLocation layerLoc = ArmorTrimItemFix.layerLoc(armorType, patternName, materialName);

                        if (ArmorTrimItemFix.isDoubleLayered(trimmable)) {
                            generators.generateLayeredItem(overrideName, trimmableKey, trimmableKey.withSuffix("_overlay"), layerLoc);
                        }
                        else {
                            generators.generateLayeredItem(overrideName, trimmableKey, layerLoc);
                        }

                        overrides.add(createOverride(overrideName.toString(), patternValue, materialValue));
                    });
                }

                if (ArmorTrimItemFix.isDoubleLayered(trimmable)) {
                    ModelTemplates.TWO_LAYERED_ITEM.create(trimmableKey, TextureMapping.layered(trimmableKey, trimmableKey.withSuffix("_overlay")), generators.output, (location, map) ->
                            getModel(overrides, location, map));
                }
                else {
                    ModelTemplates.FLAT_ITEM.create(trimmableKey, TextureMapping.layer0(trimmableKey), generators.output, (location, map) ->
                            getModel(overrides, location, map));
                }
            }
        });
    }

    private static JsonObject createOverride(String name, float patternValue, float materialValue) {
        JsonObject override = new JsonObject();
        JsonObject predicate = new JsonObject();

        predicate.addProperty(ArmorTrimItemFix.TRIM_PATTERN_PREDICATE_ID.toString(), patternValue);
        predicate.addProperty(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID.toString(), materialValue);
        override.add("predicate", predicate);
        override.addProperty("model", name);
        return override;
    }

    private static JsonObject getModel(JsonArray overrides, ResourceLocation location, Map<TextureSlot, ResourceLocation> map) {
        JsonObject model = ModelTemplates.TWO_LAYERED_ITEM.createBaseTemplate(location, map);
        model.add("overrides", overrides);
        return model;
    }
}
