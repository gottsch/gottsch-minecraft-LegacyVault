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
package mod.gottsch.forge.legacyvault.core.capability;

import mod.gottsch.forge.gottschcore.spatial.DimensionCoords;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

/**
 * Forge capability wrapper for {@link PlayerVaultData}.
 * All persistent state lives in PlayerVaultData (no loader-specific imports there).
 * On a NeoForge port, replace this class with an AttachmentType-based holder
 * that wraps the same PlayerVaultData — the data and NBT logic stay unchanged.
 *
 * @author Mark Gottschling on May 11, 2021
 */
public class PlayerVaultsHandler implements IPlayerVaultsHandler, INBTSerializable<CompoundTag> {

    private final PlayerVaultData data = new PlayerVaultData();

    @Override
    public CompoundTag serializeNBT() {
        return data.save();
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
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
