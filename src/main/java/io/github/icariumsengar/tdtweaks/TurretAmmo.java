package io.github.icariumsengar.tdtweaks;

import blusunrize.immersiveengineering.common.config.IEServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/** The free ammo Immersive Engineering's turrets fire when they are empty (see the ie mixins). */
public final class TurretAmmo {
  private TurretAmmo() {}

  /** One Casull Cartridge, the Gun Turret's free round. */
  public static ItemStack casullRound() {
    return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersiveengineering", "casull")));
  }

  /** The fluid a Chemthrower Turret sprays: the tank's, or creosote when the tank holds less than one shot. */
  public static FluidStack chemFluid(FluidStack tank) {
    int perShot = IEServerConfig.TOOLS.chemthrower_consumption.get();
    if (tank.getAmount() >= perShot) return tank;
    return new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation("immersiveengineering", "creosote")), Math.max(1000, perShot));
  }
}
