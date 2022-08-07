/**
 * 
 */
package com.someguyssoftware.legacyvault.item;

import java.util.function.Supplier;

import com.someguyssoftware.gottschcore.item.ModItem;

import mod.gottsch.forge.legacyvault.LegacyVault;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

/**
 * @author Mark Gottschling on May 3, 2021
 *
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(LegacyVault.MODID)
public class LegacyVaultItems {
	// for future use
	public static Item LOGO;
	public static Item APPLICATION;

	/**
	 * The actual event handler that registers the custom items.
	 *
	 * @param event The event this event handler handles
	 */
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		LOGO = new ModItem(LegacyVault.MODID, "legacyvault_tab", new Item.Properties());
		APPLICATION = new ModItem(LegacyVault.MODID, "vault_application", new Item.Properties().tab(CreativeModeTab.TAB_MISC));
		event.getRegistry().registerAll(
				LOGO,
				APPLICATION
				);
	}
	
	/**
	 * Register the {@link IItemColor} handlers.
	 *
	 * @param event The event
	 */
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerItemColours(final ColorHandlerEvent.Item event) {
		final BlockColors blockColors = event.getBlockColors();
		final ItemColors itemColors = event.getItemColors();

		// Use the Block's colour handler for an ItemBlock
		final ItemColor itemBlockColourHandler = (stack, tintIndex) -> {
			final BlockState state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
			return blockColors.getColor(state, null, null, tintIndex);
		};

//		itemColors.register(itemBlockColourHandler, TreasureBlocks.FALLING_GRASS);
	}
	
	/**
	 * 
	 * @author Mark Gottschling on Aug 12, 2020
	 *
	 */
	public static class ModItemGroup extends CreativeModeTab {
		private final Supplier<ItemStack> iconSupplier;

		public ModItemGroup(final String name, final Supplier<ItemStack> iconSupplier) {
			super(name);
			this.iconSupplier = iconSupplier;
		}

		@Override
		public ItemStack makeIcon() {
			return iconSupplier.get();
		}
	}
}

