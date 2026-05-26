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
package mod.gottsch.forge.legacyvault.core.crypto;

import mod.gottsch.forge.legacyvault.core.LegacyVault;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

/**
 * Loads (or generates on first use) the server-side master secret used to derive
 * per-player encryption keys for vault data.
 *
 * The secret is 32 random bytes from SecureRandom, persisted at
 * {@code config/legacyvault/legacyvault-secret.dat}. Losing this file makes all
 * encrypted vault records unreadable.
 *
 * @author Mark Gottschling
 */
public final class MasterSecretManager {
    public static final String SECRET_FILENAME = "legacyvault-secret.dat";
    private static final int SECRET_BYTES = 32;

    private static volatile byte[] cachedSecret;

    private MasterSecretManager() {}

    /**
     * Returns the master secret, loading from disk or generating a new one on first call.
     * Subsequent calls return the cached value.
     */
    public static synchronized byte[] getOrCreateSecret() throws IOException {
        if (cachedSecret != null) return cachedSecret;

        Path path = secretPath();
        if (Files.exists(path)) {
            cachedSecret = readSecret(path);
            LegacyVault.LOGGER.info("legacy vault encryption: master secret loaded");
        } else {
            byte[] secret = new byte[SECRET_BYTES];
            new SecureRandom().nextBytes(secret);
            Files.createDirectories(path.getParent());
            Files.write(path, secret);
            cachedSecret = secret;
            LegacyVault.LOGGER.info("legacy vault encryption: new master secret generated at {}", path);
        }
        return cachedSecret;
    }

    /**
     * Returns the secret if a secret file exists on disk, or null if not. Does NOT
     * generate a new one. Use this on the read path when handling a possibly-encrypted
     * file: a null return with an encrypted file present is a fatal data-recovery error.
     */
    public static synchronized byte[] getSecretIfExists() throws IOException {
        if (cachedSecret != null) return cachedSecret;
        Path path = secretPath();
        if (!Files.exists(path)) return null;
        cachedSecret = readSecret(path);
        LegacyVault.LOGGER.info("legacy vault encryption: master secret loaded");
        return cachedSecret;
    }

    private static byte[] readSecret(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        if (bytes.length != SECRET_BYTES) {
            throw new IOException("legacy vault master secret file has invalid length: " + bytes.length + " (expected " + SECRET_BYTES + ")");
        }
        return bytes;
    }

    private static Path secretPath() {
        return Paths.get(FMLPaths.CONFIGDIR.get().toString(), LegacyVault.MOD_ID)
                .toAbsolutePath()
                .resolve(SECRET_FILENAME);
    }
}
