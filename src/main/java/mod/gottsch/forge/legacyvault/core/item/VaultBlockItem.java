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
package mod.gottsch.forge.legacyvault.core.item;

import mod.gottsch.forge.gottschcore.spatial.Coords;
import mod.gottsch.forge.gottschcore.spatial.ICoords;
import mod.gottsch.forge.gottschcore.world.WorldInfo;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.CommunityVaultBlock;
import mod.gottsch.forge.legacyvault.core.capability.IPlayerVaultsHandler;
import mod.gottsch.forge.legacyvault.core.config.Config;
import mod.gottsch.forge.legacyvault.core.config.Config.ServerConfig;
import mod.gottsch.forge.legacyvault.core.network.LegacyVaultNetworking;
import mod.gottsch.forge.legacyvault.core.network.VaultCountMessageToClient;
import mod.gottsch.forge.legacyvault.core.util.LangUtil;
import mod.gottsch.forge.legacyvault.core.util.ModUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

/**
 * Don't necessarily need separate classes for Personal and Community Vault block items.
 * However, need to ensure the right object is being checked for.
 * @author Mark Gottschling on May 25, 2021
 *
 */
public class VaultBlockItem extends BlockItem {

	/**
	 *
	 * @param block
	 * @param properties
	 */
	public VaultBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag type) {
		appendBaseText(stack, level, tooltip, type);
		LangUtil.appendAdvancedHoverText(tooltip, tt -> {
			if (stack.getItem() == ModItems.COMMUNITY_VAULT.get()) {
				appendAdvancedText(stack, level, tooltip, type, "usage.community_vault");
			} else {
				appendAdvancedText(stack, level, tooltip, type, "usage.personal_vault");
			}
		});
	}


	public void appendBaseText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {

//		tooltip.add(Component.translatable(LangUtil.tooltip("mage_flame.desc")).withStyle(ChatFormatting.YELLOW));
//		tooltip.add(Component.literal(LangUtil.NEWLINE));
//		tooltip.add(Component.translatable(LangUtil.tooltip("light_level"), DynamicLights.MAGE_FLAME_LUMINANCE));
//		tooltip.add(Component.translatable(LangUtil.tooltip("lifespan"), ticksToTime(Config.SERVER.mageFlameLifespan.get())));
	}


	public void appendAdvancedText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag, String key) {
		MutableComponent lore = Component.translatable(LangUtil.tooltip(key));
		tooltip.add(Component.literal(" "));
		for (String s : lore.getString().split("~")) {
			tooltip.add(Component.translatable(LangUtil.INDENT2)
					.append(Component.literal(s).withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)));
		}
	}

	/**
	 *
	 */
	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		if (WorldInfo.isServerSide(context.getLevel())) {

			/*
			 *  Q: why add this creative check ? what is the reasoning?
			 *  A: the Block.playerDestroy() is only called in survival mode, thus the decrement vault count calculation is not executed,
			 *  which will throw off the vault count. so just making Creative actions the same for placement and destroy.
			 */

			if (context.getPlayer().isCreative()) {
				return context.getLevel().setBlock(context.getClickedPos(), state, 26);
			}

			if ((state.getBlock() instanceof CommunityVaultBlock)) {
				if (Config.ServerConfig.COMMUNITY.enabled.get()
						&& ModUtil.doesPlayerHaveCommunityAccess(context.getPlayer())) {
					return context.getLevel().setBlock(context.getClickedPos(), state, 26);
				}
			}
			else {
				// only place if personal vaults are enabled
				if (ServerConfig.PERSONAL.enabled.get()) {
					// get  player capabilities
					IPlayerVaultsHandler cap = ModUtil.getPlayerCapability(context.getPlayer());
					if (LegacyVault.LOGGER.isDebugEnabled()) {
						LegacyVault.LOGGER.debug("player vault count -> {}", cap.getCount());
					}

					if (!ServerConfig.PERSONAL.unlimitedVaults.get()) {
						if (cap != null && cap.getCount() < ServerConfig.PERSONAL.vaultsPerPlayer.get()) {
							if (LegacyVault.LOGGER.isDebugEnabled()) {
								LegacyVault.LOGGER.debug("player branch count less than config -> {}", ServerConfig.PERSONAL.vaultsPerPlayer.get());
							}

							// increment capability size
							int count = cap.getCount() + 1;
							count = count > ServerConfig.PERSONAL.vaultsPerPlayer.get() ? ServerConfig.PERSONAL.vaultsPerPlayer.get() : count;
							cap.setCount(count);

							// send state message to client
							VaultCountMessageToClient message = new VaultCountMessageToClient(context.getPlayer().getStringUUID(), count);
							LegacyVaultNetworking.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) context.getPlayer()), message);
						} else {
							LegacyVault.LOGGER.debug("player branch count greater than config-> {}", ServerConfig.PERSONAL.vaultsPerPlayer.get());
							return false;
						}
					}

					// add the vault location to capabilities
					ICoords location = new Coords(context.getClickedPos());
					cap.getLocations().add(location);

					return context.getLevel().setBlock(context.getClickedPos(), state, 26);
				}
			}
		} else {
			LegacyVault.LOGGER.debug("no can do, you're on client side");
		}

		return false;
	}
}
