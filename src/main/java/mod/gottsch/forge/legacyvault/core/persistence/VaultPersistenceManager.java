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

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.core.enums.GameType;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mark Gottschling on 2/16/2025
 */
public class VaultPersistenceManager {

    /** NBT key for the player's current vault tier inside the persisted file. */
    public static final String VAULT_TIER_KEY = "vaultTier";

    private static final Map<String, NonNullList<ItemStack>> REGISTRY = new ConcurrentHashMap<>();

    public static void clear() {
        REGISTRY.clear();
    }

    public static void remove(String key) {
        REGISTRY.remove(key);
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

    public static boolean hasVaultFile(Player player) {
        String key = generateKey(player);
        Path filePath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MOD_ID)
                .toAbsolutePath().resolve(key);
        return Files.exists(filePath) && !Files.isDirectory(filePath);
    }

    /*
     * Loads the player's vault inventory AND tier from disk.
     * Tier is applied to the player's capability so it persists cross-world (the
     * vault file is the source of truth; the capability is just a session cache).
     *
     * Tier resolution:
     *  - file present, has VaultTier tag       → use that value
     *  - file present, no VaultTier tag (legacy) → use maxTier
     *  - no file (new player)                  → use startingTier
     */
    public static void load(Player player) {
        String key = generateKey(player);
        int targetSize = ServerConfig.PERSONAL.maxTier.get() * 9;

        Optional<NonNullList<ItemStack>> optionalInventory = get(key);
        NonNullList<ItemStack> persistedInventory;
        if (optionalInventory.isEmpty()) {
            persistedInventory = NonNullList.withSize(targetSize, ItemStack.EMPTY);
            register(key, persistedInventory);
        } else {
            persistedInventory = optionalInventory.get();
        }

        int resolvedTier = ServerConfig.PERSONAL.startingTier.get();  // default for new player
        try {
            Path directoryPath = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MOD_ID).toAbsolutePath();
            Files.createDirectories(directoryPath);
            Path filePath = directoryPath.resolve(key);
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath.toFile()))) {
                    CompoundTag compound = NbtIo.readCompressed(dis);
                    if (Config.General.MAX_INVENTORY_SIZE > targetSize) {
                        // legacy file may have up to MAX_INVENTORY_SIZE slots; load into a full buffer
                        // so ContainerHelper doesn't silently drop items beyond targetSize
                        NonNullList<ItemStack> buffer = NonNullList.withSize(Config.General.MAX_INVENTORY_SIZE, ItemStack.EMPTY);
                        ContainerHelper.loadAllItems(compound, buffer);
                        for (int i = targetSize; i < Config.General.MAX_INVENTORY_SIZE; i++) {
                            if (!buffer.get(i).isEmpty()) {
                                LegacyVault.LOGGER.warn("legacy vault load: item '{}' in slot {} exceeds current maxTier slots ({}); item cannot be recovered",
                                        buffer.get(i).getHoverName().getString(), i, targetSize);
                            }
                        }
                        for (int i = 0; i < targetSize; i++) {
                            persistedInventory.set(i, buffer.get(i));
                        }
                    } else {
                        ContainerHelper.loadAllItems(compound, persistedInventory);
                    }

                    // tier — present means upgrade-system file, absent means pre-upgrade legacy file
                    resolvedTier = compound.contains(VAULT_TIER_KEY)
                            ? compound.getInt(VAULT_TIER_KEY)
                            : ServerConfig.PERSONAL.maxTier.get();
                } catch (IOException e) {
                    LegacyVault.LOGGER.error("an error occurred attempting to load vault inventory from persistence ->", e);
                }
            }
        } catch(Exception e) {
            LegacyVault.LOGGER.error("an error occurred reading a vault inventory for player ->", e);
        }

        // push resolved tier into the player's capability (session cache)
        final int finalTier = resolvedTier;
        player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                .ifPresent(cap -> cap.setVaultTier(finalTier));
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

            // tier — pulled from the session capability cache
            int tier = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                    .map(IPlayerVaultsHandler::getVaultTier)
                    .orElse(ServerConfig.PERSONAL.startingTier.get());
            compound.putInt(VAULT_TIER_KEY, tier);

            // write to a temp file first, then atomically rename to prevent corruption on crash
            Path dbDir = Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MOD_ID).toAbsolutePath();
            Path dbPath = dbDir.resolve(key);
            Path tempPath = dbDir.resolve(key + ".tmp");
            Path lockPath = dbDir.resolve(key + ".lock");
            try {
                Files.createDirectories(dbDir);
                try (FileChannel lockChannel = FileChannel.open(lockPath,
                             StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                     FileLock ignored = lockChannel.lock()) {
                    try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(tempPath.toFile()))) {
                        NbtIo.writeCompressed(compound, dos);
                    }
                    try {
                        Files.move(tempPath, dbPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                    } catch (AtomicMoveNotSupportedException e) {
                        Files.move(tempPath, dbPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (Exception e) {
                LegacyVault.LOGGER.error("an error occurred attempting to save vault inventory to persistence -> {}", key, e);
            }
        }
    }
}
