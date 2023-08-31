package einstein.armortrimitemfix.compat;

import einstein.armortrimitemfix.api.TrimRegistry;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimPatterns;

public class MinecraftCompat extends AbstractTrimCompat {

    public MinecraftCompat(String modId) {
        super(modId);
    }

    @Override
    public void init(TrimRegistry registry) {
        registry.registerPattern(TrimPatterns.SENTRY.location());
        registry.registerPattern(TrimPatterns.DUNE.location());
        registry.registerPattern(TrimPatterns.COAST.location());
        registry.registerPattern(TrimPatterns.WILD.location());
        registry.registerPattern(TrimPatterns.WARD.location());
        registry.registerPattern(TrimPatterns.EYE.location());
        registry.registerPattern(TrimPatterns.VEX.location());
        registry.registerPattern(TrimPatterns.TIDE.location());
        registry.registerPattern(TrimPatterns.SNOUT.location());
        registry.registerPattern(TrimPatterns.RIB.location());
        registry.registerPattern(TrimPatterns.SPIRE.location());
        registry.registerPattern(TrimPatterns.WAYFINDER.location());
        registry.registerPattern(TrimPatterns.SHAPER.location());
        registry.registerPattern(TrimPatterns.SILENCE.location());
        registry.registerPattern(TrimPatterns.RAISER.location());
        registry.registerPattern(TrimPatterns.HOST.location());

        registry.registerMaterial(modLoc("quartz"));
        registry.registerMaterial(modLoc("iron"), ArmorMaterials.IRON);
        registry.registerMaterial(modLoc("netherite"), ArmorMaterials.NETHERITE);
        registry.registerMaterial(modLoc("redstone"));
        registry.registerMaterial(modLoc("copper"));
        registry.registerMaterial(modLoc("gold"), ArmorMaterials.GOLD);
        registry.registerMaterial(modLoc("emerald"));
        registry.registerMaterial(modLoc("diamond"), ArmorMaterials.DIAMOND);
        registry.registerMaterial(modLoc("lapis"));
        registry.registerMaterial(modLoc("amethyst"));

        registry.registerTrimmableItem(Items.LEATHER_HELMET);
        registry.registerTrimmableItem(Items.LEATHER_CHESTPLATE);
        registry.registerTrimmableItem(Items.LEATHER_LEGGINGS);
        registry.registerTrimmableItem(Items.LEATHER_BOOTS);
        registry.registerTrimmableItem(Items.CHAINMAIL_HELMET);
        registry.registerTrimmableItem(Items.CHAINMAIL_CHESTPLATE);
        registry.registerTrimmableItem(Items.CHAINMAIL_LEGGINGS);
        registry.registerTrimmableItem(Items.CHAINMAIL_BOOTS);
        registry.registerTrimmableItem(Items.IRON_HELMET);
        registry.registerTrimmableItem(Items.IRON_CHESTPLATE);
        registry.registerTrimmableItem(Items.IRON_LEGGINGS);
        registry.registerTrimmableItem(Items.IRON_BOOTS);
        registry.registerTrimmableItem(Items.GOLDEN_HELMET);
        registry.registerTrimmableItem(Items.GOLDEN_CHESTPLATE);
        registry.registerTrimmableItem(Items.GOLDEN_LEGGINGS);
        registry.registerTrimmableItem(Items.GOLDEN_BOOTS);
        registry.registerTrimmableItem(Items.DIAMOND_HELMET);
        registry.registerTrimmableItem(Items.DIAMOND_CHESTPLATE);
        registry.registerTrimmableItem(Items.DIAMOND_LEGGINGS);
        registry.registerTrimmableItem(Items.DIAMOND_BOOTS);
        registry.registerTrimmableItem(Items.NETHERITE_HELMET);
        registry.registerTrimmableItem(Items.NETHERITE_CHESTPLATE);
        registry.registerTrimmableItem(Items.NETHERITE_LEGGINGS);
        registry.registerTrimmableItem(Items.NETHERITE_BOOTS);
        registry.registerTrimmableItem(Items.TURTLE_HELMET);
    }
}
