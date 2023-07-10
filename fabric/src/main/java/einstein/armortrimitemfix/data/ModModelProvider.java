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
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;

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

                ModelTemplates.FLAT_ITEM.create(trimmableKey, TextureMapping.layer0(baseTexture), generators.output, (location, map) -> {
                    JsonObject model = ModelTemplates.TWO_LAYERED_ITEM.createBaseTemplate(location, map);
                    JsonArray overrides = new JsonArray();

                    for (ResourceKey<TrimMaterial> material : ArmorTrimItemFix.TRIM_MATERIALS.keySet()) {
                        float materialValue = ArmorTrimItemFix.TRIM_MATERIALS.get(material);
                        String materialName = material.location().getPath();

                        for (ResourceLocation pattern : ArmorTrimItemFix.TRIM_PATTERNS.keySet()) {
                            float patternValue = ArmorTrimItemFix.TRIM_PATTERNS.get(pattern);
                            String patternName = pattern.getPath();
                            JsonObject override = new JsonObject();
                            JsonObject predicate = new JsonObject();
                            ResourceLocation overrideName = ArmorTrimItemFix.overrideName(trimmableKey, patternName, materialName);

                            generators.generateLayeredItem(overrideName, baseTexture, ArmorTrimItemFix.layerLoc(armorType, patternName, materialName));

                            predicate.addProperty(ArmorTrimItemFix.PREDICATE_ID.toString(), patternValue);
                            predicate.addProperty(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID.toString(), materialValue);
                            override.add("predicate", predicate);
                            override.addProperty("model", overrideName.toString());
                            overrides.add(override);
                        }
                    }
                    model.add("overrides", overrides);
                    return model;
                });
            }
        });
    }
}
