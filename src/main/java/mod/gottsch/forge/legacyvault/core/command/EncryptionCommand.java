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
package mod.gottsch.forge.legacyvault.core.command;

import com.mojang.brigadier.CommandDispatcher;
import mod.gottsch.forge.gottschcore.command.CommandResponseFormatter;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * /legacyvault encryption status
 * /legacyvault encryption on
 * /legacyvault encryption off
 *
 * Toggles the encryptVaultData server config at runtime. The next vault save
 * picks up the new value immediately -- no reload required.
 *
 * @author Mark Gottschling on May 20, 2026
 */
public class EncryptionCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("encryption")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("status")
                                .executes(ctx -> status(ctx.getSource())))
                        .then(Commands.literal("on")
                                .executes(ctx -> set(ctx.getSource(), true)))
                        .then(Commands.literal("off")
                                .executes(ctx -> set(ctx.getSource(), false)))));
    }

    private static int status(CommandSourceStack source) {
        boolean enabled = ServerConfig.GENERAL.encryptVaultData.get();
        List<Component> lines = CommandResponseFormatter.formatSuccess(
                Component.literal("Vault encryption status"),
                Component.literal("Currently: " + (enabled ? "ENABLED" : "DISABLED")));
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }

    private static int set(CommandSourceStack source, boolean enable) {
        try {
            ServerConfig.GENERAL.encryptVaultData.set(enable);
            Config.SERVER_SPEC.save();

            List<Component> lines;
            if (enable) {
                lines = CommandResponseFormatter.formatSuccess(
                        Component.literal("Vault encryption: ENABLED"),
                        Component.literal("New saves will be encrypted. A master secret will be created at config/" + LegacyVault.MOD_ID + "/legacyvault-secret.dat on the next save if it does not already exist -- BACK IT UP."));
            } else {
                lines = CommandResponseFormatter.formatSuccess(
                        Component.literal("Vault encryption: DISABLED"),
                        Component.literal("New saves will be written as plaintext. Existing encrypted files remain readable as long as the master secret file is present."));
            }
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 1;
        } catch (Exception e) {
            LegacyVault.LOGGER.error("Failed to toggle encryption", e);
            List<Component> lines = CommandResponseFormatter.formatFailure(
                    Component.literal("Toggle failed"),
                    Component.literal(e.getMessage() != null ? e.getMessage() : "Unknown error -- check server logs."));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }
    }
}
