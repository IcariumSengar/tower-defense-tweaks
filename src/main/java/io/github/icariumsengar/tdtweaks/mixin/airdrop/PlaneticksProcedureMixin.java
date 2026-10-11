package io.github.icariumsengar.tdtweaks.mixin.airdrop;

import net.mcreator.dyairdrop.procedures.PlaneticksProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Realistic Airdrop: the plane flies at a third of its speed, so players can see it coming and
 * follow it, and lives long enough to reach its drop point at that speed.
 */
@Mixin(value = PlaneticksProcedure.class, remap = false)
public abstract class PlaneticksProcedureMixin {
  // The tick sets the velocity to (3, 0, 0) on spawn and again every 5 ticks; both become
  // (1, 0, 0). The crate drops on distance flown, so only the speed changes.
  @ModifyConstant(method = "execute", constant = @Constant(doubleValue = 3.0), require = 2, allow = 2)
  private static double tdtweaks$slowPlane(double speed) {
    return 1.0;
  }

  // The plane also despawns at `timer >= 205` ticks, whatever its drop distance. With drag, the
  // slow plane nets ~0.84 blocks a tick and reaches the pack's 200-block drop point at tick ~238,
  // so at 205 it would vanish first and no crate would ever drop.
  @ModifyConstant(method = "execute", constant = @Constant(doubleValue = 205.0), allow = 1)
  private static double tdtweaks$laterDespawn(double ticks) {
    return 400.0;
  }
}
