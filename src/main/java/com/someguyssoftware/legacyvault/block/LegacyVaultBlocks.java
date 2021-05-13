/**
 * 
 */
package com.someguyssoftware.legacyvault.block;

import com.google.common.base.Preconditions;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.config.Config;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class LegacyVaultBlocks {

	public static Block VAULT = null;
	
	/**
	 *
	 */
	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class RegistrationHandler {

		/**
		 * 
		 * @param event
		 */
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			VAULT = new VaultBlock(LegacyVault.MODID, Config.BlockID.VAULT_ID, Block.Properties.of(Material.METAL, MaterialColor.WOOD).strength(2.5F));
			
			final IForgeRegistry<Block> registry = event.getRegistry();
			registry.register(VAULT);
		}
		
		/**
		 * Register this mod's {@link ItemBlock}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();
			BlockItem itemBlock = new BlockItem(VAULT, new Item.Properties().tab(ItemGroup.TAB_MISC));
			final ResourceLocation registryName = Preconditions.checkNotNull(VAULT.getRegistryName(),
					"Block %s has null registry name", VAULT);
			registry.register(itemBlock.setRegistryName(registryName));
		}
	}
}
