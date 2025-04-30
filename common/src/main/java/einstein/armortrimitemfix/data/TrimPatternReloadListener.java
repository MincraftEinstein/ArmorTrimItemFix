package einstein.armortrimitemfix.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static einstein.armortrimitemfix.ArmorTrimItemFix.*;

public class TrimPatternReloadListener extends SimplePreparableReloadListener<Void> {

    public static final List<ResourceLocation> TRIM_PATTERNS = new ArrayList<>();

    @Override
    protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<ResourceLocation> patterns = new ArrayList<>();
        ResourceLocation location = loc(MOD_ID + "/patterns.json");

        for (Resource resource : manager.getResourceStack(location)) {
            try (Reader reader = resource.openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                TrimPatternData data = TrimPatternData.CODEC.parse(new Dynamic<>(JsonOps.INSTANCE, element)).getOrThrow();

                if (data.replace()) {
                    patterns.clear();
                }

                data.values().forEach(pattern -> {
                    if (!patterns.contains(pattern)) {
                        patterns.add(pattern);
                    }
                });
            }
            catch (Exception e) {
                LOGGER.error("Couldn't read trim pattern list {} in data pack {}", location, resource.sourcePackId(), e);
            }
        }

        TRIM_PATTERNS.clear();
        TRIM_PATTERNS.addAll(patterns);
        return null;
    }

    @Override
    protected void apply(Void unused, ResourceManager manager, ProfilerFiller profiler) {
    }
}
