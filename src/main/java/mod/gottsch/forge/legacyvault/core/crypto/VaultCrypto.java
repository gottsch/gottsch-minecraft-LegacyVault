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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AES-256-GCM encryption helpers for vault data.
 *
 * <p>Blob format:
 * <pre>
 *   byte[0]            = VERSION_BYTE (0x01)
 *   byte[1..12]        = 12-byte IV (random per write)
 *   byte[13..n-17]     = ciphertext
 *   byte[n-16..n]      = 16-byte GCM auth tag (appended by the cipher)
 * </pre>
 *
 * <p>The 0x01 version byte is chosen so the read path can distinguish encrypted blobs
 * from legacy GZipped NBT (which always starts with 0x1F).
 *
 * @author Mark Gottschling
 */
public final class VaultCrypto {
    public static final byte VERSION_BYTE = 0x01;

    private static final int IV_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_TAG_BYTES = GCM_TAG_BITS / 8;
    private static final String CIPHER = "AES/GCM/NoPadding";

    private VaultCrypto() {}

    /** Derives the AES-256 key for a given player from the master secret. */
    public static SecretKey deriveKey(byte[] masterSecret, String playerUUID) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(masterSecret);
        sha.update((byte) ':');
        sha.update(playerUUID.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(sha.digest(), "AES");
    }

    /** Encrypts the given plaintext, returning a self-describing blob (see class javadoc). */
    public static byte[] encrypt(byte[] plaintext, SecretKey key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        if (iv.length != IV_BYTES) {
            throw new GeneralSecurityException("unexpected IV length from cipher: " + iv.length);
        }
        byte[] ct = cipher.doFinal(plaintext);

        byte[] blob = new byte[1 + IV_BYTES + ct.length];
        blob[0] = VERSION_BYTE;
        System.arraycopy(iv, 0, blob, 1, IV_BYTES);
        System.arraycopy(ct, 0, blob, 1 + IV_BYTES, ct.length);
        return blob;
    }

    /** Decrypts a blob produced by {@link #encrypt}. Throws if the version byte or auth tag is invalid. */
    public static byte[] decrypt(byte[] blob, SecretKey key) throws GeneralSecurityException {
        if (blob.length < 1 + IV_BYTES + GCM_TAG_BYTES) {
            throw new GeneralSecurityException("encrypted blob too short: " + blob.length);
        }
        if (blob[0] != VERSION_BYTE) {
            throw new GeneralSecurityException("unsupported vault encryption version: 0x" + String.format("%02X", blob[0]));
        }
        byte[] iv = new byte[IV_BYTES];
        System.arraycopy(blob, 1, iv, 0, IV_BYTES);

        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        return cipher.doFinal(blob, 1 + IV_BYTES, blob.length - 1 - IV_BYTES);
    }
}
