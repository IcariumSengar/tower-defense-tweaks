package io.github.icariumsengar.tdtweaks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;

/** Entities a Tesla Coil leaves alone (see ie.TeslaCoilBlockEntityMixin). */
public final class TeslaTargets {
  // Matched by name so this mod needs no SecurityCraft dependency.
  private static final String SENTRY = "net.geforcemods.securitycraft.entity.sentry.Sentry";

  private TeslaTargets() {}

  /**
   * Players and SecurityCraft Sentries, because IE has no notion of an owner. Armor stands, because
   * the pack's invisible marker stands are living entities, and a coil near the pedestal or a Lure
   * Block zapped them every 1.6 s at 512 FE a shot.
   */
  public static boolean isProtected(Entity entity) {
    return entity instanceof Player
        || entity instanceof ArmorStand
        || entity.getClass().getName().equals(SENTRY);
  }
}
