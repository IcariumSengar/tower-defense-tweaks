package io.github.icariumsengar.tdtweaks.mixin.airdrop;

import net.mcreator.dyairdrop.procedures.AirdroplargeticksProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Realistic Airdrop: a landed crate never deletes itself. Its block tick counts a timer and sets
 * the crate to air, loot and all, at 20000 ticks (~16.7 min); the limit becomes 1e12 ticks. (The
 * mod's other removal, "your airdrop has been stolen", has a config option.)
 */
@Mixin(value = AirdroplargeticksProcedure.class, remap = false)
public abstract class AirdroplargeticksProcedureMixin {
  @ModifyConstant(method = "execute", constant = @Constant(doubleValue = 20000.0), allow = 1)
  private static double tdtweaks$crateNeverExpires(double ticks) {
    return 1.0E12;
  }
}
