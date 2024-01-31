/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;

import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.capability.LegacyVaultCapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * @author Mark Gottschling on Jun 7, 2021
 */
public class GetVaultLocationsCommand {

    /**
     * @param dispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher
                .register(Commands.literal("vault-get-locations")
                        .requires(source -> {
                            return source.hasPermission(2);
                        })
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(source -> {
                                    return getLocations(source.getSource(), EntityArgument.getEntities(source, "targets"));
                                })
                        )
                );
    }

	/**
	 *
	 * @param source
	 * @param entities
	 * @return
	 */
    private static int getLocations(CommandSourceStack source, Collection<? extends Entity> entities) {
        LegacyVault.LOGGER.debug("getLocations command being called.");
        Entity entity = entities.iterator().next();
        LegacyVault.LOGGER.debug("entity -> {}", entity);
        if (entity instanceof Player) {
            LegacyVault.LOGGER.debug("player entity -> {}", ((Player) entity).getDisplayName());
            // get capabilities
            IPlayerVaultsHandler cap = entity.getCapability(LegacyVaultCapabilities.PLAYER_VAULTS_CAPABILITY).orElseThrow(() -> {
                return new RuntimeException("player does not have PlayerVaultsHandler capability.'");
            });

            // cycle through all the locations and print to chat
            source.sendSuccess(
                    () -> {
                        return Component.literal(((Player) entity).getDisplayName().getString() + " " + "vault locations:");
                    },
                    false
            );
            for (ICoords location : cap.getLocations()) {
                source.sendSuccess(
                        () -> {
                            return Component.literal(location.toShortString());
                        },
                        false
                );
            }
        }
        return 1;
    }
}