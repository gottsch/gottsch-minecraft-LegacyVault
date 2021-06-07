/**
 * 
 */
package com.someguyssoftware.legacyvault.capability;

import java.util.ArrayList;
import java.util.List;

import com.someguyssoftware.gottschcore.spatial.ICoords;

/**
 * @author Mark Gottschling on May 11, 2021
 *
 */
public class PlayerVaultsHandler implements IPlayerVaultsHandler {
	private int count;
	private List<ICoords> locations;

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
