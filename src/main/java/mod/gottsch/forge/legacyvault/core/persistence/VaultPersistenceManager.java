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
package mod.gottsch.forge.legacyvault.core.persistence;

import com.google.common.collect.Maps;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.enums.GameType;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mark Gottschling on 2/16/2025
 */
public class VaultPersistenceManager {

    private static final Map<String, NonNullList<ItemStack>> REGISTRY = Maps.newHashMap();

    public static void clear() {
        REGISTRY.clear();
    }

    public static void register(String key, NonNullList<ItemStack> inventory) {
        REGISTRY.put(key, inventory);
    }

    public static Optional<NonNullList<ItemStack>> get(Player player) {
        return get(generateKey(player));
    }

    public static Optional<NonNullList<ItemStack>> get(String key) {
        return Optional.ofNullable(REGISTRY.get(key));
    }

    public static String generateKey(Player player) {
        return player.getStringUUID() + "_" + LegacyVault.MC_VERSION + "_" + (LegacyVault.instance.isHardCore() ? GameType.HARDCORE.getValue() : GameType.NORMAL.getValue());
    }

    /*
     *
     */
    public static void load(Player player) {
        // generate key
        String key = generateKey(player);

        // get inventory from registry
        NonNullList<ItemStack> persistedInventory = NonNullList.createWithCapacity(100);
        Optional<NonNullList<ItemStack>> optionalInventory = get(key);
        if (optionalInventory.isEmpty()) {
            // initialize inventory with air
            for (int i = 0; i < 100; i++) {
                persistedInventory.add(new ItemStack(Items.AIR));
            }
            register(key, persistedInventory);
        } else {
            persistedInventory = optionalInventory.get();
        }

        try {
            // get the file from the file system
            Path directoryPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MOD_ID).toAbsolutePath();
            Files.createDirectories(directoryPath);
            Path filePath = directoryPath.resolve(key);
            if (Files.exists(filePath) && !Files.isDirectory(filePath, new LinkOption[]{})) {
                FileInputStream fis = new FileInputStream(filePath.toFile());
                DataInputStream dis = new DataInputStream(fis);
                CompoundTag compound = null;
                try {
                    // load data into a nbt compound
                    compound = NbtIo.readCompressed(dis);
                    // copy items from nbt compound to inventory
                    ContainerHelper.loadAllItems(compound, persistedInventory);
                } catch (IOException e) {
                    LegacyVault.LOGGER.error("an error occurred attempting to load vault inventory from persistence ->", e);
                }
               dis.close();
                fis.close();
            }
        } catch(Exception e) {
            LegacyVault.LOGGER.error("an error occurred reading a vault inventory for player ->", e);
        }
    }

    /*
     *
     */
    public static void save(Player player) {
        // generate key
        String key = generateKey(player);

        // get the NonNullList inventory from registry
        Optional<NonNullList<ItemStack>> optionalInventory = get(key);

        if (optionalInventory.isPresent()) {
            NonNullList<ItemStack> persistedInventory = optionalInventory.get();
            CompoundTag compound = new CompoundTag();
            // copy items list to nbt
            ContainerHelper.saveAllItems(compound, persistedInventory);

            // save to file, overwriting if it exists
            Path dbPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MOD_ID, key).toAbsolutePath();
            try {
                FileOutputStream fos = new FileOutputStream(dbPath.toFile());
                DataOutputStream dos = new DataOutputStream(fos);
                NbtIo.writeCompressed(compound, dos);
                dos.close();
                fos.close();
            } catch (Exception e) {
                LegacyVault.LOGGER.error("an error occurred attempting to save vault inventory to persistence -> " + key, e);
            }
        }
    }
}
