package einstein.armortrimitemfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.math.Quadrant;
import einstein.armortrimitemfix.ItemLayerKeyContext;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.CuboidRotation;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static einstein.armortrimitemfix.ArmorTrimItemFix.expand;

@Mixin(ItemModelGenerator.class)
public class ItemModelGeneratorMixin {

    @WrapOperation(
            method = {"bakeExtrudedSprite", "bakeSideFaces"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/cuboid/FaceBakery;bakeQuad(Lnet/minecraft/client/resources/model/ModelBaker$Interner;Lorg/joml/Vector3fc;Lorg/joml/Vector3fc;Lnet/minecraft/client/resources/model/cuboid/CuboidFace$UVs;Lcom/mojang/math/Quadrant;Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/block/dispatch/ModelState;Lnet/minecraft/client/resources/model/cuboid/CuboidRotation;)Lnet/minecraft/client/resources/model/geometry/BakedQuad;"
            )
    )
    private static BakedQuad bakeFrontAndBackFaces(ModelBaker.Interner interner, Vector3fc from, Vector3fc _to, CuboidFace.UVs uvs, Quadrant uvRotation, BakedQuad.MaterialInfo materialInfo, Direction facing, ModelState modelState, CuboidRotation elementRotation, Operation<BakedQuad> original) {
        int layerIndex = ItemLayerKeyContext.get();
        if (layerIndex > 0) {
            from = expand(from, layerIndex, facing != Direction.WEST);
            _to = expand(_to, layerIndex, facing == Direction.EAST);
        }
        return original.call(interner, from, _to, uvs, uvRotation, materialInfo, facing, modelState, elementRotation);
    }
}
