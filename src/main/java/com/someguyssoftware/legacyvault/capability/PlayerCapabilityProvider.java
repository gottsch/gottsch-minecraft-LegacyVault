/**
 * 
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
