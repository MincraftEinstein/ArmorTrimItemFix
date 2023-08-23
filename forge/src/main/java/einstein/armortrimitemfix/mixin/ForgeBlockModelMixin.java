package einstein.armortrimitemfix.mixin;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.UnbakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Function;

@Mixin(BlockModel.class)
public class ForgeBlockModelMixin {

    @Redirect(method = "getOverrides(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/function/Function;)Lnet/minecraft/client/renderer/block/model/ItemOverrides;", at = @At(value = "NEW", target = "(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/resources/model/UnbakedModel;Ljava/util/List;Ljava/util/function/Function;)Lnet/minecraft/client/renderer/block/model/ItemOverrides;"))
    private ItemOverrides getItemOverrides(ModelBaker baker, UnbakedModel model, List<ItemOverride> overrides, Function<Material, TextureAtlasSprite> spriteGetter) {
        List<ItemOverride> newOverrides = model instanceof BlockModel blockModel ? ArmorTrimItemFix.createOverrides(blockModel, overrides) : overrides;
        return new ItemOverrides(baker, model, newOverrides, spriteGetter);
    }
}
