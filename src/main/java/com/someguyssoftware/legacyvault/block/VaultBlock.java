/**
 * 
 */
package com.someguyssoftware.legacyvault.block;

import com.someguyssoftware.gottschcore.block.ModContainerBlock;
import com.someguyssoftware.gottschcore.world.WorldInfo;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class VaultBlock extends AbstractLegacyVaultBlock  implements ILegacyVaultBlock {
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public VaultBlock(String modID, String name, Properties properties) {
		super(modID, name, properties);
	}
	
	/**
	 * 
	 */
	@Override
	 public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		VaultTileEntity vaultTileEntity = null;
		try {
			vaultTileEntity = new VaultTileEntity();
		}
		catch(Exception e) {
			LegacyVault.LOGGER.error(e);
		}

		return vaultTileEntity;
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	/**
	 * 
	 */
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	/**
	 * 
	 */
	@Override
	   public BlockRenderType getRenderShape(BlockState state) {
	      return BlockRenderType.ENTITYBLOCK_ANIMATED;
	   }
	
	/**
	 * 
	 */
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockState = this.defaultBlockState().setValue(FACING,
				context.getHorizontalDirection().getOpposite());
		return blockState;
	}
	
	/**
	 * Called just after the player places a block.
	 */
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		LegacyVault.LOGGER.debug("Placing chest from item");

		VaultTileEntity valutTileEntity = null;

		// face the block towards the player (there isn't really a front)
		worldIn.setBlock(pos, state.setValue(FACING, placer.getDirection().getOpposite()), 3);
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity != null && tileEntity instanceof VaultTileEntity) {
			// get the backing tile entity
			valutTileEntity = (VaultTileEntity) tileEntity;

			// set the name of the chest
			if (stack.hasCustomHoverName()) {
				valutTileEntity.setCustomName(stack.getDisplayName());
			}

			// update the facing
			valutTileEntity.setFacing(placer.getDirection().getOpposite());
		}
	}
	
	/**
	 * 
	 */
	@Override
	   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {

		// exit if on the client
		if (WorldInfo.isClientSide(world)) {
			return ActionResultType.SUCCESS;
		}

		// get the container provider
		INamedContainerProvider namedContainerProvider = this.getContainer(state, world, pos);			
		// open the chest
		NetworkHooks.openGui((ServerPlayerEntity)player, namedContainerProvider, (packetBuffer)->{});
		// NOTE: (packetBuffer)->{} is just a do-nothing because we have no extra data to send

		return ActionResultType.SUCCESS;
	}
}
