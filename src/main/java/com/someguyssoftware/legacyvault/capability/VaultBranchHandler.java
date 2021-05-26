/**
 * 
 */
package com.someguyssoftware.legacyvault.capability;

/**
 * @author Mark Gottschling on May 11, 2021
 *
 */
public class VaultBranchHandler implements IVaultBranchHandler {
	private int count;

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void setCount(int size) {
		this.count = size;
	}
}
