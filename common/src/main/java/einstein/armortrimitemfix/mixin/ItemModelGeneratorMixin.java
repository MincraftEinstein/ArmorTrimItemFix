package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

import static einstein.armortrimitemfix.ArmorTrimItemFix.expand;

@Mixin(ItemModelGenerator.class)
public class ItemModelGeneratorMixin {

    @WrapOperation(method = {"processFrames", "createSideElements"}, at = @At(value = "NEW", target = "(Lorg/joml/Vector3fc;Lorg/joml/Vector3fc;Ljava/util/Map;)Lnet/minecraft/client/renderer/block/model/BlockElement;"))
    private static BlockElement createFrontAndBackFaces(Vector3fc to, Vector3fc from, Map<Direction, BlockElementFace> faces, Operation<BlockElement> original, @Local(argsOnly = true) int layerIndex) {
        return original.call(
                expand(to, layerIndex, !faces.containsKey(Direction.WEST)),
                expand(from, layerIndex, faces.containsKey(Direction.EAST)),
                faces
        );
    }
}
