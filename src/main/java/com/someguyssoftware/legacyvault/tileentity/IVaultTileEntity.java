/**
 * 
 */
package com.someguyssoftware.legacyvault.tileentity;

import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;

/**
 * @author Mark Gottschling on May 24, 2021
 *
 */
public interface IVaultTileEntity {

	String getOwnerUuid();

	void setOwnerUuid(String ownerUuid);

	void setFacing(Direction facing);

	void setFacing(int facingIndex);
	
	public ITextComponent getCustomName();
	
	public void setCustomName(ITextComponent name);

}
