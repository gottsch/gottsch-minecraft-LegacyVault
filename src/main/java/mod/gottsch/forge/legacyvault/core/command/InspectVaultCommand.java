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
import mod.gottsch.forge.gottschcore.command.ReportBuilder;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.persistence.VaultPersistenceManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * /legacyvault inspect <player>
 *
 * Shows an admin report of the target player's currently loaded vault inventory:
 * player name, UUID, vault persistence key, slot count, and all non-empty items.
 * Only resolves online players (Brigadier EntityArgument.player() constraint).
 *
 * @author Mark Gottschling on May 11, 2026
 */
public class InspectVaultCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("inspect")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> inspect(
                                        ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player"))))));
    }

    private static int inspect(CommandSourceStack source, ServerPlayer target) {
        String key = VaultPersistenceManager.generateKey(target);
        Optional<NonNullList<ItemStack>> optInv = VaultPersistenceManager.get(key);

        if (optInv.isEmpty()) {
            List<Component> lines = CommandResponseFormatter.formatFailure(
                    Component.literal("No vault data loaded for " + target.getScoreboardName()),
                    Component.literal("The player's vault was not found in the in-memory registry. "
                            + "They may need to log in first."));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }

        NonNullList<ItemStack> inv = optInv.get();
        int displaySize = inv.size();
        int occupied = 0;
        for (int i = 0; i < displaySize; i++) {
            if (!inv.get(i).isEmpty()) occupied++;
        }

        ReportBuilder builder = CommandResponseFormatter.report("Vault Inspect")
                .row("Player", target.getScoreboardName())
                .row("UUID", target.getStringUUID())
                .row("Vault Key", key)
                .row("Slots", occupied + " / " + displaySize + " occupied");

        if (occupied == 0) {
            builder.blank().note("(vault is empty)");
        } else {
            builder.section("Items");
            for (int i = 0; i < displaySize; i++) {
                ItemStack stack = inv.get(i);
                if (!stack.isEmpty()) {
                    Component value = Component.literal(stack.getCount() + "x ")
                            .withStyle(ChatFormatting.YELLOW)
                            .append(stack.getHoverName().copy().withStyle(ChatFormatting.WHITE));
                    builder.row("Slot " + i, value);
                }
            }
        }

        List<Component> lines = builder.build();
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }
}
