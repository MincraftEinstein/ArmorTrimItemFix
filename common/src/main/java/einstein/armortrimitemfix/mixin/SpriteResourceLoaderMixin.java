package einstein.armortrimitemfix.mixin;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import einstein.armortrimitemfix.ModifiableSpriteResourceLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(SpriteResourceLoader.class)
public class SpriteResourceLoaderMixin implements ModifiableSpriteResourceLoader {

    @Shadow
    @Final
    private List<SpriteSource> sources;

    @Inject(method = "load", at = @At(value = "RETURN"))
    private static void load(ResourceManager manager, ResourceLocation atlas, CallbackInfoReturnable<SpriteResourceLoader> cir) {
        ModifiableSpriteResourceLoader loader = (ModifiableSpriteResourceLoader) cir.getReturnValue();
        loader.armorTrimItemFix$modify(atlas);
    }

    @Override
    public void armorTrimItemFix$modify(ResourceLocation atlas) {
        if (atlas.equals(ArmorTrimItemFix.BLOCK_ATLAS)) {
            List<ResourceLocation> textures = new ArrayList<>();

            for (ResourceLocation holder : ArmorTrimItemFix.TRIM_PATTERNS.keySet()) {
                for (ArmorItem.Type type : ArmorItem.Type.values()) {
                    ResourceLocation texture = ArmorTrimItemFix.loc("trims/items/" + type.getName() + "_" + holder.getPath() + "_trim");
                    textures.add(texture);
                }
            }

            sources.add(PalettedPermutationsInvoker.init(textures, ArmorTrimItemFix.COLOR_PALETTE_KEY, ArmorTrimItemFix.PERMUTATIONS));
        }
    }
}
