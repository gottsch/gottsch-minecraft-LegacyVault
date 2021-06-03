/**
 * 
 */
package com.someguyssoftware.legacyvault.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * @author Mark Gottschling on May 12, 2021
 *
 */
public class VaultCountStorage implements IStorage<IVaultCountHandler> {

	@Override
	public INBT writeNBT(Capability<IVaultCountHandler> capability, IVaultCountHandler instance, Direction side) {
		return IntNBT.valueOf(instance.getCount());
	}

	@Override
	public void readNBT(Capability<IVaultCountHandler> capability, IVaultCountHandler instance, Direction side,
			INBT nbt) {
		instance.setCount(((IntNBT) nbt).getAsInt());		
	}

}
