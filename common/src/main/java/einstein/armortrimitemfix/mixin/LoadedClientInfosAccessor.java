package einstein.armortrimitemfix.mixin;

import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientItemInfoLoader.LoadedClientInfos.class)
public interface LoadedClientInfosAccessor {

    @Mutable
    @Accessor("contents")
    void setContents(Map<ResourceLocation, ClientItem> contents);
}
