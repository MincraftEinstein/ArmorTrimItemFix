package einstein.armortrimitemfix;

import einstein.armortrimitemfix.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.util.Properties;
import java.util.function.Supplier;

public class ModernFixWarningManager {

    public static final Supplier<Boolean> IS_MODERNFIX_LOADED = () -> Services.PLATFORM.isModLoaded("modernfix");
    private static final String DYNAMIC_RESOURCES = "mixin.perf.dynamic_resources";
    private static final String CONFIG_FILE = "modernfix-mixins.properties";
    private static boolean DYNAMIC_RESOURCES_ENABLED = false;
    private static boolean HAS_SHOWN_TOAST = false;

    public static void clientTick(Minecraft minecraft) {
        if (HAS_SHOWN_TOAST) {
            return;
        }

        if (minecraft.getOverlay() == null) {
            HAS_SHOWN_TOAST = true;

            if (DYNAMIC_RESOURCES_ENABLED) {
                minecraft.getToastManager().addToast(SystemToast.multiline(
                        minecraft, new SystemToast.SystemToastId(10000L),
                        Component.translatable("toast.armortrimitemfix.warning.modernfix.dynamic_resources.title"),
                        Component.translatable("toast.armortrimitemfix.warning.modernfix.dynamic_resources.description")
                ));
            }
        }
    }

    public static void load() {
        File file = new File(Services.PLATFORM.getConfigDirectory().toUri().resolve(CONFIG_FILE));
        if (file.exists()) {
            try {
                Properties properties = new Properties();
                properties.load(file.toURI().toURL().openStream());

                if (properties.containsKey(DYNAMIC_RESOURCES)) {
                    DYNAMIC_RESOURCES_ENABLED = Boolean.parseBoolean(properties.getProperty(DYNAMIC_RESOURCES));
                }
            }
            catch (Exception e) {
                ArmorTrimItemFix.LOGGER.error("Failed to read ModernFix configs", e);
            }
        }
    }
}
