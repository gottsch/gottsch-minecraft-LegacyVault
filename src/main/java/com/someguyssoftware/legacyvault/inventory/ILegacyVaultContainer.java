/**
 * 
 */
package com.someguyssoftware.legacyvault.inventory;

import net.minecraft.inventory.IInventory;

/**
 * 
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public interface ILegacyVaultContainer {

	IInventory getContents();

	void setContents(IInventory inventory);

	LegacyVaultInventory getVaultInventory();

	void setVaultInventory(LegacyVaultInventory legacyVaultInventory);

}
