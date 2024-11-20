package einstein.armortrimitemfix.data;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ArmorTrimItemFix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ArmorTrimItemFix.TRIMMABLES.forEach((trimmable, trimmableData) -> {
            ResourceLocation trimmableKey = BuiltInRegistries.ITEM.getKey(trimmable);
            if (trimmableKey != null) {
                ResourceLocation baseTexture = trimmableKey.withPrefix("item/");
                ItemModelBuilder model;
                if (ArmorTrimItemFix.isDoubleLayered(trimmable)) {
                    model = generatedItem(trimmableKey.toString(), baseTexture, baseTexture.withSuffix("_overlay"));
                }
                else {
                    model = generatedItem(trimmableKey.toString(), baseTexture);
                }

                for (ArmorTrimItemFix.MaterialData materialData : ArmorTrimItemFix.TRIM_MATERIALS) {
                    float materialValue = materialData.propertyValue();
                    String materialName = materialData.getName(trimmable);

                    model = model.override().model(getExistingFile(ArmorTrimItemFix.vanillaOverrideName(trimmableKey, materialName)))
                            .predicate(ArmorTrimItemFix.TRIM_PATTERN_PREDICATE_ID, ArmorTrimItemFix.DEFAULT_TRIM_VALUE)
                            .predicate(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, materialValue)
                            .end();

                    for (ResourceLocation pattern : ArmorTrimItemFix.TRIM_PATTERNS.keySet()) {
                        float patternValue = ArmorTrimItemFix.TRIM_PATTERNS.get(pattern);
                        String patternName = pattern.getPath();
                        String name = ArmorTrimItemFix.overrideName(trimmableData, trimmableKey, patternName, materialName).toString();
                        ResourceLocation layerLoc = ArmorTrimItemFix.layerLoc(trimmableData.type(), patternName, materialName);
                        ItemModelBuilder builder;

                        if (ArmorTrimItemFix.isDoubleLayered(trimmable)) {
                            builder = generatedItem(name, baseTexture, baseTexture.withSuffix("_overlay"), layerLoc);
                        }
                        else {
                            builder = generatedItem(name, baseTexture, layerLoc);
                        }

                        model = model.override().model(getExistingFile(builder.getLocation()))
                                .predicate(ArmorTrimItemFix.TRIM_PATTERN_PREDICATE_ID, patternValue)
                                .predicate(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, materialValue)
                                .end();
                    }
                }
            }
        });
    }

    private ItemModelBuilder generatedItem(String name, ResourceLocation... layers) {
        ItemModelBuilder model = withExistingParent(name, "item/generated");
        for (int i = 0; i < layers.length; i++) {
            model = model.texture("layer" + i, layers[i]);
        }
        return model;
    }
}
