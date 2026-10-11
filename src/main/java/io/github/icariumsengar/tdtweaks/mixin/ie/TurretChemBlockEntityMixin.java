package io.github.icariumsengar.tdtweaks.mixin.ie;

import blusunrize.immersiveengineering.common.blocks.metal.TurretChemBlockEntity;
import io.github.icariumsengar.tdtweaks.TurretAmmo;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Chemthrower Turret fires without fluid: when its tank holds less than one shot it sprays free
 * creosote. Fluid a player pumps in is used first, as usual. Power and the redstone signal are
 * still required.
 */
@Mixin(value = TurretChemBlockEntity.class, remap = false)
public abstract class TurretChemBlockEntityMixin {
  // canActivate: `tank.getFluidAmount() > 0` - the tank never reads as dry.
  @Redirect(
      method = "canActivate",
      at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/templates/FluidTank;getFluidAmount()I"),
      allow = 1)
  private int tdtweaks$neverDry(FluidTank tank) {
    return 1;
  }

  // activate: `FluidStack fs = tank.getFluid().copy()` (local 1) - creosote when short.
  @ModifyVariable(method = "activate", at = @At("STORE"), index = 1, allow = 1)
  private FluidStack tdtweaks$freeFluidIfShort(FluidStack tankFluid) {
    return TurretAmmo.chemFluid(tankFluid);
  }
}
