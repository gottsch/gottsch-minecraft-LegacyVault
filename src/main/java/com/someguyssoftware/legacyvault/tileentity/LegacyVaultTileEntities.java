/**
 * 
 */
package com.someguyssoftware.legacyvault.tileentity;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.block.LegacyVaultBlocks;
import com.someguyssoftware.legacyvault.config.Config;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class LegacyVaultTileEntities {
	/*
	 * NOTE can't have final properties as the creation of the tile entity can't
	 * happen until after the creation of the blocks. These are not *real* constants
	 * even though they have a name format like constants because of the above
	 * restriction.
	 */
	public static TileEntityType<VaultTileEntity> VAULT_TILE_ENTITY_TYPE;
	public static TileEntityType<MediumVaultTileEntity> MEDIUM_VAULT_TILE_ENTITY_TYPE;
	public static TileEntityType<LargeVaultTileEntity> LARGE_VAULT_TILE_ENTITY_TYPE;
	

	@Mod.EventBusSubscriber(modid = LegacyVault.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void onTileEntityTypeRegistration(final RegistryEvent.Register<TileEntityType<?>> event) {
			// you probably don't need a datafixer --> null should be fine
			VAULT_TILE_ENTITY_TYPE = TileEntityType.Builder.of(VaultTileEntity::new, LegacyVaultBlocks.VAULT)
					.build(null);
			MEDIUM_VAULT_TILE_ENTITY_TYPE = TileEntityType.Builder.of(MediumVaultTileEntity::new, LegacyVaultBlocks.VAULT)
					.build(null);
			LARGE_VAULT_TILE_ENTITY_TYPE = TileEntityType.Builder.of(LargeVaultTileEntity::new, LegacyVaultBlocks.VAULT)
					.build(null);
			
			// register
			VAULT_TILE_ENTITY_TYPE.setRegistryName(Config.TileEntityID.VAULT_TE_ID);
			event.getRegistry().register(VAULT_TILE_ENTITY_TYPE);
			MEDIUM_VAULT_TILE_ENTITY_TYPE.setRegistryName(Config.TileEntityID.MEDIUM_VAULT_TE_ID);
			event.getRegistry().register(MEDIUM_VAULT_TILE_ENTITY_TYPE);
			LARGE_VAULT_TILE_ENTITY_TYPE.setRegistryName(Config.TileEntityID.LARGE_VAULT_TE_ID);
			event.getRegistry().register(LARGE_VAULT_TILE_ENTITY_TYPE);
		}
	}
}
