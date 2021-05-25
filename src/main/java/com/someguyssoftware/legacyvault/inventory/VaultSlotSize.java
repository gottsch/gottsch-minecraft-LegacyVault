/**
 * 
 */
package com.someguyssoftware.legacyvault.inventory;

/**
 * @author Mark Gottschling on May 23, 2021
 *
 */
public enum VaultSlotSize {
	SMALL(27),
	MEDIUM(54),
	LARGE(80);

	private int size;
	
	VaultSlotSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
	
}
