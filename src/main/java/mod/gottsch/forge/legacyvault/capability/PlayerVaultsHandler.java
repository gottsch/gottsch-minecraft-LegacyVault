/*
 * This file is part of  Treasure2.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
 *
 * Legacy Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Legacy Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Legacy Vault.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.legacyvault.capability;

import java.util.ArrayList;
import java.util.List;

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
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
