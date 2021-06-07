/**
 * 
 */
package com.someguyssoftware.legacyvault.capability;

import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * @author Mark Gottschling on May 12, 2021
 *
 */
public class PlayerVaultsStorage implements IStorage<IPlayerVaultsHandler> {
	private static final String COUNT = "count";
	private static final String LOCATIONS = "locations";
	
	@Override
	public INBT writeNBT(Capability<IPlayerVaultsHandler> capability, IPlayerVaultsHandler instance, Direction side) {
//		return IntNBT.valueOf(instance.getCount());
		
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt(COUNT, instance.getCount());

		ListNBT list = new ListNBT();
		for (ICoords location :  instance.getLocations()) {
			CompoundNBT coords = new CompoundNBT();
			coords.putInt("x", location.getX());
			coords.putInt("y", location.getY());
			coords.putInt("z", location.getZ());
			list.add(coords);
		}
		nbt.put(LOCATIONS, list);
		return nbt;
	}

	@Override
	public void readNBT(Capability<IPlayerVaultsHandler> capability, IPlayerVaultsHandler instance, Direction side,
			INBT nbt) {
//		instance.setCount(((IntNBT) nbt).getAsInt());
		if (nbt instanceof CompoundNBT) {
			CompoundNBT compound = (CompoundNBT)nbt;
			if (compound.contains(COUNT)) {
				instance.setCount(compound.getInt(COUNT));
			}
			
			// add locations
			if (compound.contains(LOCATIONS)) {
				ListNBT locations = compound.getList(LOCATIONS, 10);
				for (INBT loc : locations) {
					ICoords coords = new Coords(((CompoundNBT)loc).getInt("x"), ((CompoundNBT)loc).getInt("y"), ((CompoundNBT)loc).getInt("z") );
					instance.getLocations().add(coords);
				}
			}
		}
	}
}
