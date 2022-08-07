/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022, Mark Gottschling (gottsch)
 * 
 * All rights reserved.
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
package com.someguyssoftware.legacyvault.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

/**
 * @author Mark Gottschling on May 24, 2021
 *
 */
public interface IVaultBlockEntity {

	String getOwnerUuid();

	void setOwnerUuid(String ownerUuid);

	void setFacing(Direction facing);

	void setFacing(int facingIndex);
	
	public Component getCustomName();
	
	public void setCustomName(Component name);
	
	public float getOpenNess(float partialTicks);

}
