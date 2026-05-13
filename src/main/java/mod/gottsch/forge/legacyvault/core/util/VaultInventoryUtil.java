/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

/**
 * Shared utility methods for copying and clearing vault inventories.
 *
 * @author Mark Gottschling on May 11, 2026
 */
public final class VaultInventoryUtil {

    private VaultInventoryUtil() {}

    /**
     * Copies all slots from a NonNullList into an IItemHandler, stopping at whichever
     * boundary is reached first. Used when loading a persisted inventory into the
     * open container.
     */
    public static void copyToHandler(NonNullList<ItemStack> source, IItemHandler dest) {
        for (int i = 0; i < source.size(); i++) {
            if (i >= dest.getSlots()) break;
            dest.insertItem(i, source.get(i), false);
        }
    }

    /**
     * Copies all slots from an IItemHandler back into a NonNullList, stopping at
     * whichever boundary is reached first. Used when saving the container back to
     * the persisted inventory on close.
     * NOTE: uses set() not add() — add() appends and does not update existing slots.
     */
    public static void copyFromHandler(IItemHandler source, NonNullList<ItemStack> dest) {
        for (int i = 0; i < source.getSlots(); i++) {
            if (i >= dest.size()) break;
            dest.set(i, source.getStackInSlot(i));
        }
    }

    /**
     * Copies all slots from one NonNullList into another using ItemStack.copy(),
     * stopping at whichever boundary is reached first.
     */
    public static void copy(NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
        for (int i = 0; i < from.size(); i++) {
            if (i >= to.size()) break;
            to.set(i, from.get(i).copy());
        }
    }

    /**
     * Moves all slots from one NonNullList into another (copy + clear source),
     * stopping at whichever boundary is reached first.
     */
    public static void move(NonNullList<ItemStack> from, NonNullList<ItemStack> to) {
        for (int i = 0; i < from.size(); i++) {
            if (i >= to.size()) break;
            to.set(i, from.get(i).copy());
            from.set(i, ItemStack.EMPTY);
        }
    }

    /**
     * Sets every slot in the list to ItemStack.EMPTY.
     */
    public static void clear(NonNullList<ItemStack> inv) {
        for (int i = 0; i < inv.size(); i++) {
            inv.set(i, ItemStack.EMPTY);
        }
    }
}
