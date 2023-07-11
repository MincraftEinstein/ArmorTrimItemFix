package einstein.armortrimitemfix.data;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ArmorTrimItemFix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ArmorTrimItemFix.TRIMMABLES.forEach((trimmable, armorType) -> {
            ResourceLocation trimmableKey = ForgeRegistries.ITEMS.getKey(trimmable);
            if (trimmableKey != null) {
                ResourceLocation baseTexture = trimmableKey.withPrefix("item/");
                ItemModelBuilder model;
                if (ArmorTrimItemFix.isDoubleLayered(trimmable)) {
                    model = generatedItem(trimmableKey.toString(), baseTexture, baseTexture.withSuffix("_overlay"));
                }
                else {
                    model = generatedItem(trimmableKey.toString(), baseTexture);
                }

                for (ResourceKey<TrimMaterial> material : ArmorTrimItemFix.TRIM_MATERIALS.keySet()) {
                    float materialValue = ArmorTrimItemFix.TRIM_MATERIALS.get(material);
                    String materialName = material.location().getPath();

                    for (ResourceLocation pattern : ArmorTrimItemFix.TRIM_PATTERNS.keySet()) {
                        float patternValue = ArmorTrimItemFix.TRIM_PATTERNS.get(pattern);
                        String patternName = pattern.getPath();
                        String name = ArmorTrimItemFix.overrideName(trimmableKey, patternName, materialName).toString();
                        ResourceLocation layerLoc = ArmorTrimItemFix.layerLoc(armorType, patternName, materialName);
                        ItemModelBuilder builder;

                        if (ArmorTrimItemFix.isDoubleLayered(trimmable)) {
                            builder = generatedItem(name, baseTexture, baseTexture.withSuffix("_overlay"), layerLoc);
                        }
                        else {
                            builder = generatedItem(name, baseTexture, layerLoc);
                        }

                        model = model.override().model(getExistingFile(builder.getLocation()))
                                .predicate(ArmorTrimItemFix.PREDICATE_ID, patternValue)
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
