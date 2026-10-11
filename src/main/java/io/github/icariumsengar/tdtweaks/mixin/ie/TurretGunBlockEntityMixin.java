package io.github.icariumsengar.tdtweaks.mixin.ie;

import blusunrize.immersiveengineering.common.blocks.metal.TurretGunBlockEntity;
import io.github.icariumsengar.tdtweaks.TurretAmmo;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Gun Turret fires without ammo: an empty ammo slot fires a free Casull Cartridge that leaves no
 * casing (a full casing slot would stop the turret). Ammo a player loads is fired first and used
 * up as usual. Power and the redstone signal are still required.
 *
 * <p>SRG names: m_41619_ is ItemStack#isEmpty, f_41583_ is ItemStack.EMPTY.
 */
@Mixin(value = TurretGunBlockEntity.class, remap = false)
public abstract class TurretGunBlockEntityMixin {
  @Unique
  private boolean tdtweaks$freeRound;

  // canActivate: `!inventory.get(0).isEmpty()` - the ammo slot never reads as empty.
  @Redirect(
      method = "canActivate",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;m_41619_()Z"),
      allow = 1)
  private boolean tdtweaks$neverEmpty(ItemStack ammo) {
    return false;
  }

  // activate: `ItemStack stack = inventory.get(0)` (local 2) - a free round when the slot is empty.
  @ModifyVariable(method = "activate", at = @At("STORE"), index = 2, allow = 1)
  private ItemStack tdtweaks$freeRoundIfEmpty(ItemStack loaded) {
    tdtweaks$freeRound = loaded.m_41619_();
    return tdtweaks$freeRound ? TurretAmmo.casullRound() : loaded;
  }

  // activate: `ItemStack casing = bullet.getCasing(stack)` (local 4) - none from a free round.
  @ModifyVariable(method = "activate", at = @At("STORE"), index = 4, allow = 1)
  private ItemStack tdtweaks$noCasingFromFreeRound(ItemStack casing) {
    return tdtweaks$freeRound ? ItemStack.f_41583_ : casing;
  }
}
