/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * @author Mark Gottschling on 2/21/2025
 */
public class CommunityVaultContainerMenu extends VaultContainerMenu {
    /**
     * @param containerId
     * @param pos
     * @param playerInventory
     * @param player
     */
    public CommunityVaultContainerMenu(int containerId, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContainers.COMMUNITY_VAULT_CONTAINER.get(), containerId, pos, playerInventory, player);

    }
}
