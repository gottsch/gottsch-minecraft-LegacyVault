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
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.core.persistence.VaultPersistenceManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import mod.gottsch.forge.legacyvault.core.util.VaultInventoryUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * /legacyvault transfer <from> <to>
 *
 * Moves all vault contents from one online player to another, replacing the
 * target's vault and clearing the source's vault. Both players must be online.
 *
 * @author Mark Gottschling on May 11, 2026
 */
public class TransferVaultCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("transfer")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("from", EntityArgument.player())
                                .then(Commands.argument("to", EntityArgument.player())
                                        .executes(ctx -> transfer(
                                                ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "from"),
                                                EntityArgument.getPlayer(ctx, "to")))))));
    }

    private static int transfer(CommandSourceStack source, ServerPlayer from, ServerPlayer to) {
        String fromKey = VaultPersistenceManager.generateKey(from);
        String toKey   = VaultPersistenceManager.generateKey(to);

        Optional<NonNullList<ItemStack>> fromOpt = VaultPersistenceManager.get(fromKey);
        if (fromOpt.isEmpty()) {
            LegacyVault.LOGGER.warn("transfer: no vault data loaded for source {}", from.getScoreboardName());
            List<Component> lines = CommandResponseFormatter.formatFailure(
                    Component.literal("No vault data loaded for " + from.getScoreboardName()),
                    Component.literal("The source player may need to log in first."));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }

        // Ensure destination vault exists in the registry
        NonNullList<ItemStack> toInv = VaultPersistenceManager.get(toKey).orElseGet(() -> {
            NonNullList<ItemStack> fresh = NonNullList.withSize(ServerConfig.PERSONAL.maxTier.get() * 9, ItemStack.EMPTY);
            VaultPersistenceManager.register(toKey, fresh);
            return fresh;
        });

        NonNullList<ItemStack> fromInv = fromOpt.get();

        VaultInventoryUtil.move(fromInv, toInv);

        VaultPersistenceManager.save(from);
        VaultPersistenceManager.save(to);

        List<Component> lines = CommandResponseFormatter.formatSuccess(
                Component.literal("Vault transferred"),
                Component.literal("Contents moved from " + from.getScoreboardName()
                        + " to " + to.getScoreboardName() + ". Source vault is now empty."));
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }
}
