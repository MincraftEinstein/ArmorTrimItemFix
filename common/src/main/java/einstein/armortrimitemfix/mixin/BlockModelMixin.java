package einstein.armortrimitemfix.mixin;

import einstein.armortrimitemfix.ArmorTrimItemFix;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(BlockModel.class)
public class BlockModelMixin {

    @Redirect(method = "getItemOverrides", at = @At(value = "NEW", target = "(Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/renderer/block/model/BlockModel;Ljava/util/List;)Lnet/minecraft/client/renderer/block/model/ItemOverrides;"))
    private ItemOverrides getItemOverrides(ModelBaker baker, BlockModel model, List<ItemOverride> overrides) {
        return new ItemOverrides(baker, model, ArmorTrimItemFix.createOverrides(model, overrides));
    }
}
