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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
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
                List<PatternMaterialData> patternMaterialMap = new ArrayList<>();

                for (ArmorTrimItemFix.MaterialData materialData : ArmorTrimItemFix.TRIM_MATERIALS) {
                    float materialValue = materialData.propertyValue();
                    String materialName = materialData.getName(trimmable);

                    for (ResourceLocation pattern : ArmorTrimItemFix.TRIM_PATTERNS.keySet()) {
                        float patternValue = ArmorTrimItemFix.TRIM_PATTERNS.get(pattern);
                        String patternName = pattern.getPath();
                        ResourceLocation name = ArmorTrimItemFix.overrideName(trimmableKey, patternName, materialName);
                        ResourceLocation layerLoc = ArmorTrimItemFix.layerLoc(armorType, patternName, materialName);

                        if (ArmorTrimItemFix.isDoubleLayered(trimmable)) {
                            generators.generateLayeredItem(name, trimmableKey, trimmableKey.withSuffix("_overlay"), layerLoc);
                        }
                        else {
                            generators.generateLayeredItem(name, trimmableKey, layerLoc);
                        }

                        patternMaterialMap.add(new PatternMaterialData(patternName, patternValue, materialName, materialValue));
                    }
                }

                if (ArmorTrimItemFix.isDoubleLayered(trimmable)) {
                    ModelTemplates.TWO_LAYERED_ITEM.create(trimmableKey, TextureMapping.layered(trimmableKey, trimmableKey.withSuffix("_overlay")), generators.output, (location, map) ->
                            createOverrides(trimmableKey, patternMaterialMap, location, map));
                }
                else {
                    ModelTemplates.FLAT_ITEM.create(trimmableKey, TextureMapping.layer0(trimmableKey), generators.output, (location, map) ->
                            createOverrides(trimmableKey, patternMaterialMap, location, map));
                }
            }
        });
    }

    @NotNull
    private static JsonObject createOverrides(ResourceLocation name, List<PatternMaterialData> patternMaterialMap, ResourceLocation location, Map<TextureSlot, ResourceLocation> map) {
        JsonObject model = ModelTemplates.TWO_LAYERED_ITEM.createBaseTemplate(location, map);
        JsonArray overrides = new JsonArray();

        patternMaterialMap.forEach(data -> {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();

            predicate.addProperty(ArmorTrimItemFix.PREDICATE_ID.toString(), data.patternValue());
            predicate.addProperty(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID.toString(), data.materialValue());
            override.add("predicate", predicate);
            override.addProperty("model", ArmorTrimItemFix.overrideName(name, data.patternName(), data.MaterialName()).toString());
            overrides.add(override);
        });

        model.add("overrides", overrides);
        return model;
    }

    private record PatternMaterialData(String patternName, float patternValue, String MaterialName, float materialValue) {

    }
}
