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
public class VaultBranchStorage implements IStorage<IVaultBranchHandler> {

	@Override
	public INBT writeNBT(Capability<IVaultBranchHandler> capability, IVaultBranchHandler instance, Direction side) {
		return IntNBT.valueOf(instance.getCount());
	}

	@Override
	public void readNBT(Capability<IVaultBranchHandler> capability, IVaultBranchHandler instance, Direction side,
			INBT nbt) {
		instance.setCount(((IntNBT) nbt).getAsInt());		
	}

}
