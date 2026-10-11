package io.github.icariumsengar.tdtweaks.mixin.ie;

import blusunrize.immersiveengineering.common.blocks.metal.TeslaCoilBlockEntity;
import io.github.icariumsengar.tdtweaks.TeslaTargets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tesla Coil: never targets or shocks players, SecurityCraft Sentries or armor stands
 * (TeslaTargets), and makes no idle arcs.
 *
 * <p>Minecraft members carry their SRG names (m_45976_ is Level#getEntitiesOfClass), the names
 * Forge 1.20.1 runs with, since this mod ships without a refmap.
 */
@Mixin(value = TeslaCoilBlockEntity.class, remap = false)
public abstract class TeslaCoilBlockEntityMixin {
  // tickServer fetches the entities around the coil once, then picks its target from that list
  // and applies the residual field to the rest, so filtering the list covers both.
  @Redirect(
      method = "tickServer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"),
      allow = 1)
  private List<Entity> tdtweaks$withoutProtected(Level level, Class<Entity> type, AABB area) {
    List<Entity> entities = new ArrayList<>();
    for (Entity entity : level.m_45976_(type, area)) {
      if (!TeslaTargets.isProtected(entity)) entities.add(entity);
    }
    return entities;
  }

  // With no target, the coil sends a visible, audible arc to a random nearby block every 6.4 s,
  // which reads as firing at nothing. IE has no option to turn it off.
  @Inject(method = "sendFreePacket", at = @At("HEAD"), cancellable = true)
  private void tdtweaks$noIdleArc(double x, double y, double z, CallbackInfo ci) {
    ci.cancel();
  }
}
