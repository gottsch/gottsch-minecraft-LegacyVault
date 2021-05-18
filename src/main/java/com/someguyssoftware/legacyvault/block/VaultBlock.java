/**
 * 
 */
package com.someguyssoftware.legacyvault.block;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public class VaultBlock extends AbstractVaultBlock  implements ILegacyVaultBlock {
	private static final VoxelShape VAULT = Block.box(1, 1, 1, 15, 16, 14);
	private static final VoxelShape FOOT1 = Block.box(1, 0, 1, 3, 1, 3);
	private static final VoxelShape FOOT2 = Block.box(13, 0, 1, 15, 1, 3);
	private static final VoxelShape FOOT3 = Block.box(1, 0, 13, 3, 1, 15);
	private static final VoxelShape FOOT4 = Block.box(13, 0, 13, 15, 1, 15);
	
	private static final VoxelShape AABB = VoxelShapes.or(VAULT, FOOT1, FOOT2, FOOT3, FOOT4);
	
	/**
	 * 
	 * @param modID
	 * @param name
	 * @param properties
	 */
	public VaultBlock(String modID, String name, Properties properties) {
		super(modID, name, properties);
		
		// set the default shapes/shape
		VoxelShape shape = AABB;
		setBounds(
				new VoxelShape[] {
						shape, 	// N
						shape,  	// E
						shape,  	// S
						shape		// W
				});
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
}
