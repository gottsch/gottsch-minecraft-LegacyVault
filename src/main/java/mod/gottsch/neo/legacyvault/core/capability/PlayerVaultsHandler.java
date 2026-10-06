/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.neo.legacyvault.core.capability;

import mod.gottsch.neo.gottschcore.spatial.DimensionCoords;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.List;

/**
 * NeoForge attachment holder for {@link PlayerVaultData} (registered in {@link ModAttachments}).
 * All persistent state lives in PlayerVaultData (no loader-specific imports there).
 *
 * @author Mark Gottschling on May 11, 2021
 */
public class PlayerVaultsHandler implements IPlayerVaultsHandler, INBTSerializable<CompoundTag> {

    private final PlayerVaultData data = new PlayerVaultData();

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return data.save();
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compound) {
        data.load(compound);
    }

    @Override
    public int getCount() { return data.getCount(); }

    @Override
    public void setCount(int size) { data.setCount(size); }

    @Override
    public List<DimensionCoords> getLocations() { return data.getLocations(); }

    @Override
    public void setLocations(List<DimensionCoords> locations) { data.setLocations(locations); }

    @Override
    public int getVaultTier() { return data.getVaultTier(); }

    @Override
    public void setVaultTier(int tier) { data.setVaultTier(tier); }
}
