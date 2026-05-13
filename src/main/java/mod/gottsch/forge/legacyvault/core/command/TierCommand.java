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
import com.mojang.brigadier.arguments.IntegerArgumentType;
import mod.gottsch.forge.gottschcore.command.CommandResponseFormatter;
import mod.gottsch.forge.gottschcore.command.ReportBuilder;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.capability.LegacyVaultCapabilities;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.List;

/**
 * /legacyvault tier inspect <player>          — show tier info for one player
 * /legacyvault tier set_tier <targets> <value> — set vaultTier to a specific value (clamped)
 * /legacyvault tier reset <targets>            — set vaultTier back to startingTier
 * /legacyvault tier max <targets>              — set vaultTier to maxTier
 *
 * @author Mark Gottschling on May 13, 2026
 */
public class TierCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("legacyvault")
                .then(Commands.literal("tier")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("inspect")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> inspect(
                                                ctx.getSource(),
                                                EntityArgument.getPlayer(ctx, "player")))))
                        .then(Commands.literal("set_tier")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                .executes(ctx -> setTier(
                                                        ctx.getSource(),
                                                        EntityArgument.getEntities(ctx, "targets"),
                                                        IntegerArgumentType.getInteger(ctx, "value"),
                                                        "set")))))
                        .then(Commands.literal("reset")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(ctx -> setTier(
                                                ctx.getSource(),
                                                EntityArgument.getEntities(ctx, "targets"),
                                                ServerConfig.PERSONAL.startingTier.get(),
                                                "reset"))))
                        .then(Commands.literal("max")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(ctx -> setTier(
                                                ctx.getSource(),
                                                EntityArgument.getEntities(ctx, "targets"),
                                                ServerConfig.PERSONAL.maxTier.get(),
                                                "max"))))));
    }

    private static int inspect(CommandSourceStack source, ServerPlayer target) {
        IPlayerVaultsHandler cap = target.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                .orElse(null);
        if (cap == null) {
            List<Component> lines = CommandResponseFormatter.formatFailure(
                    Component.literal("No vault capability for " + target.getScoreboardName()),
                    Component.literal("The player has no PlayerVaultsHandler capability attached."));
            lines.forEach(line -> source.sendSuccess(() -> line, false));
            return 0;
        }

        int tier = cap.getVaultTier();
        int maxTier = ServerConfig.PERSONAL.maxTier.get();
        int startingTier = ServerConfig.PERSONAL.startingTier.get();

        List<Component> lines = CommandResponseFormatter.report("Vault Tier")
                .row("Player", target.getScoreboardName())
                .row("Current Tier", tier + " / " + maxTier)
                .row("Active Slots", String.valueOf(tier * 9))
                .row("Starting Tier", String.valueOf(startingTier))
                .row("Max Tier", String.valueOf(maxTier))
                .build();
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return 1;
    }

    private static int setTier(CommandSourceStack source, Collection<? extends Entity> entities,
                               int requested, String label) {
        int maxTier = ServerConfig.PERSONAL.maxTier.get();
        int clamped = Math.max(0, Math.min(requested, maxTier));
        int updated = 0;

        for (Entity entity : entities) {
            if (!(entity instanceof Player player)) continue;

            IPlayerVaultsHandler cap = player.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY)
                    .orElse(null);
            if (cap == null) {
                LegacyVault.LOGGER.warn("tier {}: player {} has no vault capability", label, player.getName().getString());
                continue;
            }
            cap.setVaultTier(clamped);
            LegacyVault.LOGGER.debug("tier {}: set vaultTier for {} to {}", label, player.getName().getString(), clamped);
            updated++;
        }

        int finalUpdated = updated;
        int finalTier = clamped;
        String detail = (clamped == requested)
                ? "Set to tier " + finalTier + " for " + finalUpdated + " player(s). Players must reopen their vault for the change to take effect."
                : "Requested " + requested + " clamped to " + finalTier + " for " + finalUpdated + " player(s). Players must reopen their vault for the change to take effect.";
        List<Component> lines = CommandResponseFormatter.formatSuccess(
                Component.literal("Vault tier updated"),
                Component.literal(detail));
        lines.forEach(line -> source.sendSuccess(() -> line, false));
        return updated > 0 ? 1 : 0;
    }
}
