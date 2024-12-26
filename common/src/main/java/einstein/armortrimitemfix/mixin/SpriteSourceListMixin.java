package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import einstein.armortrimitemfix.ArmorTrimItemFix;
import einstein.armortrimitemfix.data.EquipmentType;
import einstein.armortrimitemfix.data.TrimMaterialReloadListener;
import einstein.armortrimitemfix.data.TrimPatternReloadListener;
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

            TrimMaterialReloadListener.TRIM_MATERIALS.forEach(materialData -> {
                ResourceLocation materialId = materialData.materialId();
                permutations.put(materialId.getPath(), materialId.withPrefix("trims/color_palettes/"));
                materialData.overrides().forEach((equipmentAsset, overrideName) -> {
                    permutations.put(overrideName, ResourceLocation.fromNamespaceAndPath(materialId.getNamespace(), "trims/color_palettes/" + overrideName));
                });
            });

            TrimPatternReloadListener.TRIM_PATTERNS.forEach(patternData -> {
                for (int i = 0; i < EquipmentType.values().length; i++) {
                    EquipmentType type = EquipmentType.values()[i];
                    ResourceLocation patternId = patternData.pattern();
                    String namespace = patternId.getNamespace();
                    String typeName = type.getSerializedName();
                    textures.add(ResourceLocation.fromNamespaceAndPath(
                            namespace.equals(ResourceLocation.DEFAULT_NAMESPACE) ? ArmorTrimItemFix.MOD_ID : namespace,
                            "trims/items/" + typeName + "/" + typeName + "_" + patternId.getPath() + "_trim")
                    );
                }
            });

            sources.add(new PalettedPermutations(textures, ArmorTrimItemFix.PALETTE_KEY, permutations));
        }
        return original.call(sources);
    }
}
