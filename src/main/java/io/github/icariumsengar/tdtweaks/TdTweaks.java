package io.github.icariumsengar.tdtweaks;

import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tower Defense Tweaks: runtime changes to other mods for the Tower Defense modpack, made with
 * mixins (tdtweaks.mixins.json) instead of edited copies of their jars. Each mixin applies only
 * when the mod it targets is installed (TdTweaksMixinPlugin).
 */
@Mod(TdTweaks.MOD_ID)
public final class TdTweaks {
  public static final String MOD_ID = "tdtweaks";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
}
