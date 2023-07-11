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
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.ArrayList;
import java.util.List;

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
            ResourceLocation trimmableKey = BuiltInRegistries.ITEM.getKey(trimmable);
            if (trimmableKey != null) {
                ResourceLocation baseTexture = trimmableKey.withPrefix("item/");
                List<PatternMaterialData> patternMaterialMap = new ArrayList<>();

                for (ResourceKey<TrimMaterial> material : ArmorTrimItemFix.TRIM_MATERIALS.keySet()) {
                    float materialValue = ArmorTrimItemFix.TRIM_MATERIALS.get(material);
                    String materialName = material.location().getPath();

                    for (ResourceLocation pattern : ArmorTrimItemFix.TRIM_PATTERNS.keySet()) {
                        float patternValue = ArmorTrimItemFix.TRIM_PATTERNS.get(pattern);
                        String patternName = pattern.getPath();

                        generators.generateLayeredItem(ArmorTrimItemFix.overrideName(trimmableKey, patternName, materialName), baseTexture, ArmorTrimItemFix.layerLoc(armorType, patternName, materialName));
                        patternMaterialMap.add(new PatternMaterialData(patternName, patternValue, materialName, materialValue));
                    }
                }

                ModelTemplates.FLAT_ITEM.create(trimmableKey, TextureMapping.layer0(baseTexture), generators.output, (location, map) -> {
                    JsonObject model = ModelTemplates.TWO_LAYERED_ITEM.createBaseTemplate(location, map);
                    JsonArray overrides = new JsonArray();

                    patternMaterialMap.forEach(data -> {
                        JsonObject override = new JsonObject();
                        JsonObject predicate = new JsonObject();

                        predicate.addProperty(ArmorTrimItemFix.PREDICATE_ID.toString(), data.patternValue());
                        predicate.addProperty(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID.toString(), data.materialValue());
                        override.add("predicate", predicate);
                        override.addProperty("model", ArmorTrimItemFix.overrideName(trimmableKey, data.patternName(), data.MaterialName()).toString());
                        overrides.add(override);
                    });

                    model.add("overrides", overrides);
                    return model;
                });
            }
        });
    }

    private record PatternMaterialData(String patternName, float patternValue, String MaterialName, float materialValue) {

    }
}
