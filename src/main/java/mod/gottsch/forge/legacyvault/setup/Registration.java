/*
 * This file is part of  Treasure2.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.setup;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.block.VaultBlock;
import mod.gottsch.forge.legacyvault.block.entity.VaultBlockEntity;
import mod.gottsch.forge.legacyvault.config.Config;
import mod.gottsch.forge.legacyvault.inventory.VaultContainerMenu;
import mod.gottsch.forge.legacyvault.item.VaultBlockItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 
 * @author Mark Gottschling on Jun 15, 2022
 *
 */
public class Registration {

	/*
	 * deferred registries
	 */
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LegacyVault.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LegacyVault.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LegacyVault.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LegacyVault.MODID);

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LegacyVault.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, LegacyVault.MODID);
//    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, LegacyVault.MODID);
    
	// blocks
	public static final RegistryObject<VaultBlock> VAULT = Registration.BLOCKS.register(Config.BlockID.VAULT_ID, () -> new VaultBlock(Block.Properties.of(Material.METAL, MaterialColor.WOOD).strength(2.5F)));
	
	// items
	public static final RegistryObject<Item> VAULT_ITEM = fromBlock(VAULT);
	public static final RegistryObject<Item> APPLICATION = Registration.ITEMS.register("vault_application", () -> new Item(new Item.Properties()));
	// block entities
	public static final RegistryObject<BlockEntityType<VaultBlockEntity>> VAULT_BLOCK_ENTITY_TYPE;
	// containers
	public static final RegistryObject<MenuType<VaultContainerMenu>> VAULT_CONTAINER;
	// recipes
//	public static final RegistryObject<RecipeSerializer<Recipe<?>>>
//	private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, LegacyVault.MODID);

//	public static final RegistryObject<RecipeSerializer<?>> EXAMPLE_LOOT_ITEM_CONDITION_TYPE = REGISTER.register("example_loot_item_condition_type", () -> VaultEasyDifficultyCondition.Serializer.INSTANCE);
	
	static {
		VAULT_BLOCK_ENTITY_TYPE = BLOCK_ENTITIES.register(Config.BlockEntityID.VAULT_TE_ID, () -> BlockEntityType.Builder.of(VaultBlockEntity::new, VAULT.get()).build(null));
				
		VAULT_CONTAINER = MENUS.register(Config.ContainerID.VAULT_CONTAINER,
	            () -> IForgeMenuType.create((windowId, inventory, data) -> new VaultContainerMenu(windowId, data.readBlockPos(), inventory, inventory.player)));			
	}
	
	/**
	 * 
	 */
	public static void init() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(eventBus);
		ITEMS.register(eventBus);	
		BLOCK_ENTITIES.register(eventBus);
		MENUS.register(eventBus);
		ENTITIES.register(eventBus);		
		PARTICLES.register(eventBus);		
	}
		
    /*
     * author: McJty
     *  conveniance method: take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
     */
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return Registration.ITEMS.register(block.getId().getPath(), () -> new VaultBlockItem(block.get(), new Item.Properties()));
    }
}
