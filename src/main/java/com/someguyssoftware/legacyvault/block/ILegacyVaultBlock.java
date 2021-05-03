package com.someguyssoftware.legacyvault.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

/**
 * 
 * @author Mark Gottschling on May 1, 2021
 *
 */
public interface ILegacyVaultBlock {
	
	public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);

	/**
	 * Convenience method.
	 * @param state
	 * @return
	 */
	public static Direction getFacing(BlockState state) {
		return state.getValue(FACING);
	}

	VoxelShape[] getBounds();

	ILegacyVaultBlock setBounds(VoxelShape[] bounds);
}
