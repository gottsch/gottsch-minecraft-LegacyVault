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
import mod.gottsch.forge.gottschcore.command.FormatterConstants;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.persistence.VaultPersistenceManager;
import net.minecraft.commands.CommandSourceStack;
import mod.gottsch.forge.legacyvault.core.util.VaultInventoryUtil;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * /legacyvault clear <player>          — shows a warning prompt with a clickable confirm button
 * /legacyvault clear <player> confirm  — permanently wipes the player's vault inventory
 *
 * @author Mark Gottschling on May 11, 2026
 */
public class ClearVaultCommand {

    private static final String CONFIRM = "confirm";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("clear")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> prompt(
                                        ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player")))
                                .then(Commands.literal(CONFIRM)
                                        .executes(ctx -> clear(
                                                ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "player")))))));
    }

    private static int prompt(CommandSourceStack source, ServerPlayer target) {
        String confirmCmd = "/legacyvault clear " + target.getGameProfile().getName() + " " + CONFIRM;
        Component confirmButton = FormatterConstants.buildConfirmButton(confirmCmd);

        List<Component> lines = CommandResponseFormatter.formatConfirmPrompt(
                Component.literal("Wipe vault for " + target.getScoreboardName() + "?"),
                Component.literal("This will permanently delete all items in their vault."),
                confirmButton);
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }

    private static int clear(CommandSourceStack source, ServerPlayer target) {
        String key = VaultPersistenceManager.generateKey(target);
        Optional<NonNullList<ItemStack>> optInv = VaultPersistenceManager.get(key);

        if (optInv.isEmpty()) {
            LegacyVault.LOGGER.warn("clear: no vault data loaded for {}", target.getScoreboardName());
            List<Component> lines = CommandResponseFormatter.formatFailure(
                    Component.literal("No vault data loaded for " + target.getScoreboardName()),
                    Component.literal("The player may need to log in first."));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }

        NonNullList<ItemStack> inv = optInv.get();
        VaultInventoryUtil.clear(inv);
        VaultPersistenceManager.save(target);

        List<Component> lines = CommandResponseFormatter.formatSuccess(
                Component.literal("Vault cleared for " + target.getScoreboardName()),
                Component.literal("All items have been permanently deleted."));
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }
}
