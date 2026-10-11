package io.github.icariumsengar.tdtweaks.mixin.dismemberment;

import io.github.icariumsengar.tdtweaks.TdTweaks;
import me.ichun.mods.mobdismemberment.client.core.EventHandlerClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mob Dismemberment: an exception while splitting a dying mob into gibs no longer crashes the
 * client. It is logged once, and that mob just dies without gibs.
 */
@Mixin(value = EventHandlerClient.class, remap = false)
public abstract class EventHandlerClientMixin {
  @Unique
  private boolean tdtweaks$inGuard;
  @Unique
  private boolean tdtweaks$warned;

  @Shadow
  public abstract boolean dismember(ClientLevel level, LivingEntity living, Entity source);

  // Calls dismember again from inside a try/catch. The nested call sees the flag and runs the
  // original body.
  @Inject(method = "dismember", at = @At("HEAD"), cancellable = true)
  private void tdtweaks$guard(ClientLevel level, LivingEntity living, Entity source, CallbackInfoReturnable<Boolean> cir) {
    if (tdtweaks$inGuard) return;
    tdtweaks$inGuard = true;
    try {
      cir.setReturnValue(dismember(level, living, source));
    } catch (Exception | LinkageError e) {
      if (!tdtweaks$warned) {
        tdtweaks$warned = true;
        TdTweaks.LOGGER.warn("Mob Dismemberment failed on {}; it dies without gibs (logged once)", living, e);
      }
      cir.setReturnValue(false);
    } finally {
      tdtweaks$inGuard = false;
    }
  }
}
