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
package mod.gottsch.neo.legacyvault.core.capability;

import mod.gottsch.neo.legacyvault.core.LegacyVault;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Player data attachments (replaces the Forge 1.20.1 PLAYER_VAULTS_CAPABILITY).
 *
 * @author Mark Gottschling on May 11, 2021
 */
public class ModAttachments {
	private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, LegacyVault.MOD_ID);

	/*
	 * same id as the Forge capability. copyOnDeath so count/locations/tier survive respawn
	 * (the Forge version had no PlayerEvent.Clone handler and lost them on death).
	 */
	public static final Supplier<AttachmentType<PlayerVaultsHandler>> PLAYER_VAULTS =
			ATTACHMENTS.register("playervaults", () -> AttachmentType.serializable(PlayerVaultsHandler::new).copyOnDeath().build());

	public static void register(IEventBus eventBus) {
		ATTACHMENTS.register(eventBus);
	}

	/**
	 * Attachments are created on first access, so this is never empty. It returns an Optional
	 * so the call sites keep their LazyOptional-era shape (orElse / map / ifPresent).
	 */
	public static Optional<IPlayerVaultsHandler> getPlayerVaults(Player player) {
		return Optional.of(player.getData(PLAYER_VAULTS));
	}
}
