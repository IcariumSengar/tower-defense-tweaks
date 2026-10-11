package io.github.icariumsengar.tdtweaks;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Skips a mixin when the mod it targets isn't installed, so a missing mod is not a crash. Mob
 * Dismemberment is client-only, for one, so its mixin never applies on a dedicated server. A mixin's
 * subpackage names its target mod.
 */
public final class TdTweaksMixinPlugin implements IMixinConfigPlugin {
  private static final Map<String, String> TARGET_MODS = Map.of(
      "ie", "immersiveengineering",
      "airdrop", "dyairdrop",
      "dismemberment", "mobdismemberment");

  private String mixinPackage;

  @Override
  public void onLoad(String mixinPackage) {
    this.mixinPackage = mixinPackage + ".";
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    String subpackage = mixinClassName.substring(mixinPackage.length(), mixinClassName.lastIndexOf('.'));
    String modId = TARGET_MODS.get(subpackage);
    if (modId == null) throw new IllegalStateException("No target mod for mixin " + mixinClassName);
    return LoadingModList.get().getModFileById(modId) != null;
  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
