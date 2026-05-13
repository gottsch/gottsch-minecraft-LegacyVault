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
import mod.gottsch.forge.legacyvault.core.persistence.VaultPersistenceManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * /legacyvault reload <player>
 *
 * Forces a reload of the target player's vault inventory from the NBT file on disk,
 * discarding whatever is currently in the in-memory registry for that player.
 * Useful after manually editing a vault file or restoring a backup.
 *
 * @author Mark Gottschling on May 11, 2026
 */
public class ReloadVaultCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> reload(
                                        ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player"))))));
    }

    private static int reload(CommandSourceStack source, ServerPlayer target) {
        String key = VaultPersistenceManager.generateKey(target);
        try {
            // Remove the cached entry so load() rebuilds it from disk.
            VaultPersistenceManager.remove(key);
            VaultPersistenceManager.load(target);

            List<Component> lines = CommandResponseFormatter.formatSuccess(
                    Component.literal("Vault reloaded for " + target.getScoreboardName()),
                    Component.literal("The in-memory vault has been replaced with the file at: " + key));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 1;
        } catch (Exception e) {
            LegacyVault.LOGGER.error("Failed to reload vault for player {}", target.getScoreboardName(), e);
            List<Component> lines = CommandResponseFormatter.formatFailure(
                    Component.literal("Reload failed for " + target.getScoreboardName()),
                    Component.literal(e.getMessage() != null ? e.getMessage() : "Unknown error — check server logs."));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }
    }
}
