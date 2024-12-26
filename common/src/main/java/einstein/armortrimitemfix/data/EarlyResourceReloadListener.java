package einstein.armortrimitemfix.data;

import com.mojang.serialization.Codec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public abstract class EarlyResourceReloadListener<T> extends SimpleJsonResourceReloadListener<T> {

    protected EarlyResourceReloadListener(Codec<T> codec, FileToIdConverter lister) {
        super(codec, lister);
    }

    @Override
    protected Map<ResourceLocation, T> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, T> map = super.prepare(manager, profiler);
        earlyApply(map, manager);
        return map;
    }

    protected abstract void earlyApply(Map<ResourceLocation, T> map, ResourceManager manager);

    @Override
    protected void apply(Map<ResourceLocation, T> map, ResourceManager manager, ProfilerFiller profiler) {
    }
}
