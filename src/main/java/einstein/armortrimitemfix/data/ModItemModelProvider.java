package einstein.armortrimitemfix.data;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ModItemModelProvider extends ItemModelProvider {

    public static final List<Item> TRIMMABLES = new ArrayList<>();
    public static final List<Item> TRIM_MATERIALS = Util.make(new ArrayList<>(), list -> {
        list.add(Items.AMETHYST_SHARD);
    });
    public static final List<ResourceKey<TrimPattern>> PROPERTY_BY_PATTERN = Util.make(new ArrayList<>(), list -> {
        list.add(TrimPatterns.SILENCE);
    });

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ArmorTrimItemFix.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (Item trimmable : TRIMMABLES) {
            ResourceLocation trimmableKey = ForgeRegistries.ITEMS.getKey(trimmable);
            if (trimmableKey != null) {
                List<ResourceLocation> modelBuilders = new ArrayList<>();
                for (Item material : TRIM_MATERIALS) {
                    ResourceLocation materialKey = ForgeRegistries.ITEMS.getKey(material);
                    if (materialKey != null) {
                        PROPERTY_BY_PATTERN.forEach(pattern -> {
                            ItemModelBuilder builder = generatedItem(trimmableKey.getPath() + "_" + pattern.location().getPath() + "_" + materialKey.getPath() + "_trim");
                            modelBuilders.add(builder.getLocation());
                        });
                    }
                }
                ItemModelBuilder model = generatedItem(trimmableKey.toString(), trimmableKey.withPrefix("item/"));
                float f = 0;
                for (ResourceLocation overrideModel : modelBuilders) {
                    model = model.override().model(getExistingFile(overrideModel)).predicate(ArmorTrimItemFix.PREDICATE_ID, f).end();
                    f += 0.0001F;
                }
            }
        }
    }

    private ItemModelBuilder generatedItem(String name, ResourceLocation... layers) {
        ItemModelBuilder model = withExistingParent(name, "item/generated");
        for (int i = 0; i < layers.length; i++) {
            model = model.texture("layer" + i, layers[i]);
        }
        return model;
    }
}
