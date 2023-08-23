package einstein.armortrimitemfix.mixin;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(BlockModel.class)
public class BlockModelMixin {

    @Redirect(method = "getItemOverrides", at = @At(value = "NEW", target = "(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/List;)Lnet/minecraft/client/renderer/block/model/ItemOverrides;"))
    private ItemOverrides getItemOverrides(ModelBaker baker, BlockModel model, List<ItemOverride> overrides) {
        ResourceLocation modelLoc = ResourceLocation.tryParse(model.name);
        if (modelLoc != null) {
            String path = modelLoc.getPath();
            ResourceLocation itemId = path.contains("/") ? new ResourceLocation(modelLoc.getNamespace(), path.substring(path.lastIndexOf("/") + 1)) : modelLoc;
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item instanceof ArmorItem armor && ArmorTrimItemFix.TRIMMABLES.containsKey(armor)) {
                ArmorTrimItemFix.TRIM_PATTERNS.forEach((pattern, value) -> {
                    for (ArmorTrimItemFix.MaterialData data : ArmorTrimItemFix.TRIM_MATERIALS) {
                        overrides.add(new ItemOverride(ArmorTrimItemFix.overrideName(itemId, pattern.getPath(), data.getName(item)).withPrefix("item/"), List.of(new ItemOverride.Predicate(ArmorTrimItemFix.TRIM_PATTERN_PREDICATE_ID, value), new ItemOverride.Predicate(ItemModelGenerators.TRIM_TYPE_PREDICATE_ID, data.propertyValue()))));
                    }
                });
            }
        }

        return new ItemOverrides(baker, model, overrides);
    }
}
