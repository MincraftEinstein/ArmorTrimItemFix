package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import einstein.armortrimitemfix.data.EquipmentType;
import einstein.armortrimitemfix.data.TrimDataReloadManager;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static einstein.armortrimitemfix.ArmorTrimItemFix.*;

@Mixin(SpriteSourceList.class)
public class SpriteSourceListMixin {

    @WrapOperation(method = "load", at = @At(value = "NEW", target = "(Ljava/util/List;)Lnet/minecraft/client/renderer/texture/atlas/SpriteSourceList;"))
    private static SpriteSourceList add(List<SpriteSource> sources, Operation<SpriteSourceList> original, @Local(argsOnly = true) Identifier atlasSprite) {
        if (atlasSprite.equals(AtlasIds.ITEMS)) {
            Map<String, Identifier> permutations = new HashMap<>();
            List<Identifier> textures = new ArrayList<>();

            TrimDataReloadManager.TRIM_MATERIALS.forEach(materialData -> {
                addColorPalette(permutations, materialData.materialId());
                materialData.overrides().forEach((overrideId, overrideMaterial) ->
                        addColorPalette(permutations, overrideMaterial));
            });

            TrimDataReloadManager.TRIM_PATTERNS.forEach(patternId -> {
                for (EquipmentType type : EquipmentType.values()) {
                    textures.add(getTextureId(type, patternId));
                }
            });

            sources.add(new PalettedPermutations(textures, PALETTE_KEY, permutations));
        }
        return original.call(sources);
    }
}
