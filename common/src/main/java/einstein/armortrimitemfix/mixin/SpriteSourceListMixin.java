package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(SpriteSourceList.class)
public class SpriteSourceListMixin {

    @WrapOperation(method = "load", at = @At(value = "NEW", target = "(Ljava/util/List;)Lnet/minecraft/client/renderer/texture/atlas/SpriteSourceList;"))
    private static SpriteSourceList add(List<SpriteSource> sources, Operation<SpriteSourceList> original, @Local(argsOnly = true) ResourceLocation atlasSprite) {
        if (atlasSprite.equals(ArmorTrimItemFix.BLOCKS_ATLAS)) {
            Map<String, ResourceLocation> permutations = new HashMap<>();
            List<ResourceLocation> textures = new ArrayList<>();

            ArmorTrimItemFix.TRIM_MATERIALS.forEach(materialData -> {
                ResourceLocation materialId = materialData.key().location();
                permutations.put(materialId.getPath(), materialId.withPrefix("trims/color_palettes/"));
                materialData.overrides().forEach((equipmentAsset, overrideName) -> {
                    permutations.put(overrideName, ResourceLocation.fromNamespaceAndPath(materialId.getNamespace(), "trims/color_palettes/" + overrideName));
                });
            });

            ArmorTrimItemFix.TRIM_PATTERNS.forEach(patternKey -> {
                ArmorTrimItemFix.ARMOR_TYPES.forEach(type -> {
                    ResourceLocation patternId = patternKey.location();
                    String namespace = patternId.getNamespace();
                    String typeName = type.getName();
                    textures.add(ResourceLocation.fromNamespaceAndPath(
                            namespace.equals(ResourceLocation.DEFAULT_NAMESPACE) ? ArmorTrimItemFix.MOD_ID : namespace,
                            "trims/items/" + typeName + "/" + typeName + "_" + patternId.getPath() + "_trim")
                    );
                });
            });

            sources.add(new PalettedPermutations(textures, ArmorTrimItemFix.PALETTE_KEY, permutations));
        }
        return original.call(sources);
    }
}
