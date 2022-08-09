/**
 * 
 */
package com.someguyssoftware.legacyvault.capability;

import java.util.ArrayList;
import java.util.List;

import com.someguyssoftware.gottschcore.spatial.Coords;
import com.someguyssoftware.gottschcore.spatial.ICoords;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author Mark Gottschling on May 11, 2021
 *
 */
public class PlayerVaultsHandler implements IPlayerVaultsHandler, INBTSerializable<Tag> {
	private static final String COUNT = "count";
	private static final String LOCATIONS = "locations";

	private int count;
	private List<ICoords> locations;

	@Override
	public Tag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt(COUNT, getCount());

		ListTag list = new ListTag();
		for (ICoords location :  getLocations()) {
			CompoundTag coords = new CompoundTag();
			coords.putInt("x", location.getX());
			coords.putInt("y", location.getY());
			coords.putInt("z", location.getZ());
			list.add(coords);
		}
		nbt.put(LOCATIONS, list);
		return nbt;
	}

	@Override
	public void deserializeNBT(Tag tag) {
		if (tag instanceof CompoundTag) {

			CompoundTag compound = (CompoundTag)tag;
			if (compound.contains(COUNT)) {
				setCount(compound.getInt(COUNT));
			}

			// add locations
			if (compound.contains(LOCATIONS)) {
				ListTag locations = compound.getList(LOCATIONS, 10);
				for (Tag loc : locations) {
					ICoords coords = new Coords(((CompoundTag)loc).getInt("x"), ((CompoundTag)loc).getInt("y"), ((CompoundTag)loc).getInt("z") );
					getLocations().add(coords);
				}
			}	
		}
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void setCount(int size) {
		this.count = size;
	}

	@Override
	public List<ICoords> getLocations() {
		if (locations == null) {
			locations = new ArrayList<>();
		}
		return locations;
	}

	@Override
	public void setLocations(List<ICoords> locations) {
		this.locations = locations;
	}
}
