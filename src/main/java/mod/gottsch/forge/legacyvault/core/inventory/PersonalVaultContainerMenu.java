
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

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.core.persistence.VaultPersistenceManager;
import mod.gottsch.forge.legacyvault.core.util.VaultInventoryUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Mark Gottschling on 2/21/2025
 */
public class PersonalVaultContainerMenu extends VaultContainerMenu {

    /** Number of vault rows shown at one time in the scrollable viewport. */
    public static final int VISIBLE_ROWS = 5;

    // index 0 = vaultTier, index 1 = maxTier — synced server→client automatically.
    // On the client, the first sync fires set(0, ...) which refreshes slot active flags
    // by replaying scrollTo(currentFirstRow) — without that, slots stay locked from initial build.
    private final ContainerData syncedData = new ContainerData() {
        @Override public int get(int i) { return i == 0 ? vaultTier : maxTier; }
        @Override public void set(int i, int v) {
            if (i == 0) {
                vaultTier = v;
                if (!slots.isEmpty()) scrollTo(currentFirstRow);
            } else {
                maxTier = v;
            }
        }
        @Override public int getCount() { return 2; }
    };

    private int maxTier;
    private int vaultTier;
    private int currentFirstRow = 0;

    public PersonalVaultContainerMenu(int containerId, BlockPos pos, Inventory playerInventory, Player player) {
        super(ModContainers.PERSONAL_VAULT_CONTAINER.get(), containerId, pos, playerInventory, player);
        // addDataSlots must be called after super() so the slots list is already built
        addDataSlots(syncedData);
    }

    @Override
    protected void initLayout() {
        setMenuInventoryYPos(42);      // title(6) + search bar(20) + gap(16)
        setPlayerInventoryYPos(144);   // 42 + VISIBLE_ROWS*18(90) + gap(12)
        setHotbarYPos(202);            // 144 + 3*18(54) + gap(4)
    }

    /**
     * Registers ALL maxTier×9 slots. Rows beyond VISIBLE_ROWS are placed at y=-2000
     * (off-screen, no interaction). Each slot's backing inventory index (slot.slot)
     * matches its natural row*9+col position and never changes — only its on-screen
     * position changes when scrolling. This keeps client and server in lockstep.
     */
    @Override
    public void buildContainerInventory() {
        if (getVaultInventory() == null) {
            LegacyVault.LOGGER.info("vaultInventory is null");
            return;
        }

        maxTier = ServerConfig.PERSONAL.maxTier.get();
        vaultTier = getPlayerEntity()
                .getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                .map(IPlayerVaultsHandler::getVaultTier)
                .orElseGet(() -> {
                    LegacyVault.LOGGER.warn("PersonalVaultContainerMenu: player missing vault capability; defaulting to startingTier");
                    return ServerConfig.PERSONAL.startingTier.get();
                });

        setMenuInventoryRowCount(maxTier);
        setMenuInventoryColumnCount(9);

        int activeSlots = vaultTier * 9;
        for (int row = 0; row < maxTier; row++) {
            for (int col = 0; col < 9; col++) {
                int invSlot = row * 9 + col;
                int xpos = getMenuInventoryXPos() + col * getSlotXSpacing();
                int ypos = (row < VISIBLE_ROWS)
                        ? getMenuInventoryYPos() + row * getSlotYSpacing()
                        : -2000;
                boolean active = invSlot < activeSlots;
                addSlot(new VaultSlot(getVaultInventory(), invSlot, xpos, ypos, active));
            }
        }
    }

    /**
     * Replaces each slot with a fresh copy at its scroll-adjusted position.
     * slot.slot (backing inventory index) is preserved — only x/y change.
     * Slots outside the viewport are placed at y=-2000.
     */
    public void scrollTo(int firstVisibleRow) {
        currentFirstRow = firstVisibleRow;
        int activeSlots = vaultTier * 9;
        int firstSlot = getContainerFirstSlotIndex();
        for (int row = 0; row < maxTier; row++) {
            for (int col = 0; col < 9; col++) {
                int menuIndex = firstSlot + row * 9 + col;
                int invSlot = row * 9 + col;  // STABLE — never changes
                int viewportRow = row - firstVisibleRow;
                int newX = getMenuInventoryXPos() + col * getSlotXSpacing();
                int newY = (viewportRow >= 0 && viewportRow < VISIBLE_ROWS)
                        ? getMenuInventoryYPos() + viewportRow * getSlotYSpacing()
                        : -2000;
                boolean active = invSlot < activeSlots;
                VaultSlot newSlot = new VaultSlot(getVaultInventory(), invSlot, newX, newY, active);
                ((net.minecraft.world.inventory.Slot) newSlot).index = menuIndex;  // SlotItemHandler shadows Slot.index with a private field
                slots.set(menuIndex, newSlot);
            }
        }
    }

    public void sortInventory(Player player) {
        int activeSlots = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                .map(IPlayerVaultsHandler::getVaultTier)
                .orElse(0) * 9;
        activeSlots = Math.min(activeSlots, getVaultContainer().getContainerSize());

        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < activeSlots; i++) {
            ItemStack stack = getVaultContainer().getItem(i);
            if (!stack.isEmpty()) items.add(stack.copy());
        }
        items.sort(Comparator.comparing(stack -> stack.getHoverName().getString().toLowerCase(Locale.ROOT)));
        for (int i = 0; i < activeSlots; i++) {
            getVaultContainer().setItem(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }

        Optional<NonNullList<ItemStack>> optInv = VaultPersistenceManager.get(player);
        optInv.ifPresent(inv -> VaultInventoryUtil.copyFromHandler(getVaultInventory(), inv));
        VaultPersistenceManager.save(player);
        broadcastChanges();
    }

    public int getMaxTier()      { return maxTier; }
    public int getVaultTier()    { return vaultTier; }
    public int getCurrentFirstRow() { return currentFirstRow; }
}
