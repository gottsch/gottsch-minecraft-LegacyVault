/**
 * 
 */
package com.someguyssoftware.legacyvault.capability;

/**
 * @author Mark Gottschling on May 11, 2021
 *
 */
public class VaultBranchHandler implements IVaultBranchHandler {
	private int size;

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public void setSize(int size) {
		this.size = size;
	}
}
