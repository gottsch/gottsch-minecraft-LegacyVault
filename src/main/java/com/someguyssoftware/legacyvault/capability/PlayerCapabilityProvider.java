/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package com.someguyssoftware.legacyvault.capability;

import static com.someguyssoftware.legacyvault.capability.LegacyVaultCapabilities.VAULT_BRANCH;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 * @author Mark Gottschling on May 12, 2021
 *
 */
public class PlayerCapabilityProvider implements ICapabilitySerializable<IntNBT> {

	private final LazyOptional<IVaultBranchHandler> handler = LazyOptional
			.of(VAULT_BRANCH::getDefaultInstance);

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return VAULT_BRANCH.orEmpty(cap, handler);
	}

	@Override
	public IntNBT serializeNBT() {
		return (IntNBT) VAULT_BRANCH.getStorage().writeNBT(VAULT_BRANCH,
				handler.orElseThrow(() -> new IllegalArgumentException("at serialize")), null);
	}

	@Override
	public void deserializeNBT(IntNBT nbt) {
		VAULT_BRANCH.getStorage().readNBT(VAULT_BRANCH, handler.orElseThrow(() -> new IllegalArgumentException("at deserialize")), null, nbt);
	}

}
