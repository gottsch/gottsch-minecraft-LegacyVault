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
package mod.gottsch.forge.legacyvault.core.capability;

import mod.gottsch.forge.gottschcore.spatial.DimensionCoords;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Plain-Java holder for all persistent player-vault state.
 * No loader-specific (Forge/NeoForge) imports — swap only the capability
 * wrapper when porting, not this class.
 *
 * @author Mark Gottschling on May 2026
 */
public class PlayerVaultData {

    // NBT keys — package-private so capability adapters on any loader can reference them
    static final String COUNT = "count";
    static final String LOCATIONS = "locations";
    static final String VAULT_TIER = "vaultTier";

    private int count;
    private List<DimensionCoords> locations;
    // 0 is the unset sentinel: on first login after the upgrade system is added,
    // PlayerEventHandler assigns startingTier (new player) or maxTier (legacy player).
    private int vaultTier;

    public CompoundTag save() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(COUNT, count);
        nbt.putInt(VAULT_TIER, vaultTier);

        ListTag list = new ListTag();
        for (DimensionCoords location : getLocations()) {
            CompoundTag coords = new CompoundTag();
            location.save(coords);
            list.add(coords);
        }
        nbt.put(LOCATIONS, list);
        return nbt;
    }

    public void load(CompoundTag nbt) {
        if (nbt.contains(COUNT)) {
            count = nbt.getInt(COUNT);
        }
        if (nbt.contains(VAULT_TIER)) {
            vaultTier = nbt.getInt(VAULT_TIER);
        }
        if (nbt.contains(LOCATIONS)) {
            ListTag locationList = nbt.getList(LOCATIONS, Tag.TAG_COMPOUND);
            for (Tag loc : locationList) {
                if (loc instanceof CompoundTag locTag) {
                    if (DimensionCoords.EMPTY.load(locTag) instanceof DimensionCoords dc) {
                        getLocations().add(dc);
                    }
                }
            }
        }
    }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public List<DimensionCoords> getLocations() {
        if (locations == null) {
            locations = new ArrayList<>();
        }
        return locations;
    }
    public void setLocations(List<DimensionCoords> locations) { this.locations = locations; }

    public int getVaultTier() { return vaultTier; }
    public void setVaultTier(int vaultTier) { this.vaultTier = vaultTier; }
}
