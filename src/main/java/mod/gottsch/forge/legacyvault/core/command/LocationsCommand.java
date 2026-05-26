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
import mod.gottsch.forge.gottschcore.spatial.DimensionCoords;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * /legacyvault locations <player>         — list a player's vault block positions
 * /legacyvault locations clear <player>   — clear a player's vault location list
 *
 * @author Mark Gottschling on May 11, 2026
 */
public class LocationsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("locations")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> listLocations(
                                        ctx.getSource(),
                                        EntityArgument.getPlayer(ctx, "player"))))
                        .then(Commands.literal("clear")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> clearLocations(
                                                ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "player")))))));
    }

    private static int listLocations(CommandSourceStack source, ServerPlayer target) {
        IPlayerVaultsHandler cap = target.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                .orElse(null);
        if (cap == null) {
            LegacyVault.LOGGER.warn("locations: player {} has no vault capability", target.getScoreboardName());
            List<Component> lines = CommandResponseFormatter.formatFailure(
                    Component.literal("No vault capability found for " + target.getScoreboardName()));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }

        List<DimensionCoords> locations = cap.getLocations();
        var report = CommandResponseFormatter.report(target.getScoreboardName() + " Vault Locations")
                .note(locations.size() + " location(s) tracked")
                .section("Positions");
        if (locations.isEmpty()) {
            report.note("none");
        } else {
            for (int i = 0; i < locations.size(); i++) {
                report.row("#" + (i + 1), locations.get(i).toShortString());
            }
        }
        report.build().forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }

    private static int clearLocations(CommandSourceStack source, ServerPlayer target) {
        IPlayerVaultsHandler cap = target.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                .orElse(null);
        if (cap == null) {
            LegacyVault.LOGGER.warn("locations clear: player {} has no vault capability", target.getScoreboardName());
            List<Component> lines = CommandResponseFormatter.formatFailure(
                    Component.literal("No vault capability found for " + target.getScoreboardName()));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }

        int count = cap.getLocations().size();
        cap.getLocations().clear();

        List<Component> lines = CommandResponseFormatter.formatSuccess(
                Component.literal("Locations cleared for " + target.getScoreboardName()),
                Component.literal(count + " vault location(s) removed."));
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }
}
