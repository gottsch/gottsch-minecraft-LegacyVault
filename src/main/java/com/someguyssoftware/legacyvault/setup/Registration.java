package com.someguyssoftware.legacyvault.setup;

import com.someguyssoftware.legacyvault.config.Config;
import com.someguyssoftware.legacyvault.inventory.VaultContainerMenu;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.block.VaultBlock;
import mod.gottsch.forge.legacyvault.block.entity.VaultBlockEntity;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
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
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, LegacyVault.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LegacyVault.MODID);

	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, LegacyVault.MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, LegacyVault.MODID);
	
    // item properties convenience property
	public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(CreativeModeTab.TAB_MISC);
	
	// TODO move these out to their respective classes

	// blocks
	public static final RegistryObject<VaultBlock> VAULT = BLOCKS.register(Config.BlockID.VAULT_ID, () -> new VaultBlock(Block.Properties.of(Material.METAL, MaterialColor.WOOD).strength(2.5F)));
	
	// items
	public static final RegistryObject<Item> VAULT_ITEM = fromBlock(VAULT);
	public static final RegistryObject<Item> APPLICATION = Registration.ITEMS.register("vault_application", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
	// block entities
	public static final RegistryObject<BlockEntityType<VaultBlockEntity>> VAULT_BLOCK_ENTITY_TYPE;
	// containers
	public static final RegistryObject<MenuType<VaultContainerMenu>> VAULT_CONTAINER;
	
	static {
		VAULT_BLOCK_ENTITY_TYPE = BLOCK_ENTITIES.register(Config.BlockEntityID.VAULT_TE_ID, () -> BlockEntityType.Builder.of(VaultBlockEntity::new, VAULT.get()).build(null));
				
		VAULT_CONTAINER = CONTAINERS.register(Config.ContainerID.VAULT_CONTAINER,
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
		CONTAINERS.register(eventBus);
		ENTITIES.register(eventBus);		
		PARTICLES.register(eventBus);		
	}
		
    /*
     * author: McJty
     *  conveniance method: take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
     */
    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
