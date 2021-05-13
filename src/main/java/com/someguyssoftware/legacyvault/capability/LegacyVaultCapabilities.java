/**
 * 
 */
package com.someguyssoftware.legacyvault.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * @author Mark Gottschling on May 11, 2021
 *
 */
public class LegacyVaultCapabilities {
	/*
	 * NOTE Ensure to use interfaces in @CapabilityInject, the static capability and in the instance.
	 */
	@CapabilityInject(IVaultBranchHandler.class)
    public static Capability<IVaultBranchHandler> VAULT_BRANCH = null;
}
