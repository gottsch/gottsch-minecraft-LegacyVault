/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author Mark Gottschling on Feb 20, 2025
 *
 */
public class CommunityVaultBlockEntity extends AbstractVaultBlockEntity {

	/*
	 * The Vault block entity does NOT contain an IItemHandler as it will never hold
	 * any real inventory. The inventory is pulled from the database on per user basis and
	 * the changes in the client container screen do not need to be reflected to the
	 * back end entity.
	 */

	/*
	 * Client updated variables
	 */
	/** the current angle of the lid (between 0 and 1) */
	public float lidAngle;
	/** the angle of the lid last tick */
	public float prevLidAngle;

	/**
	 *
	 * @param pos
	 * @param state
	 */
	public CommunityVaultBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.COMMUNITY_VAULT.get(), pos, state);
	}

	public void tickClient() {
		this.prevLidAngle = this.lidAngle;
		if (this.openCount > 0 && this.lidAngle == 0.0F) {
			this.playSound(SoundEvents.CHEST_OPEN);
		}

		if (this.openCount == 0 && this.lidAngle > 0.0F || this.openCount > 0 && this.lidAngle < 1.0F) {
			float f2 = this.lidAngle;

			if (this.openCount > 0) {
				this.lidAngle += 0.1F;
			} else {
				this.lidAngle -= 0.1F;
			}

			if (this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			//float f3 = 0.5F;
			if (this.lidAngle < 0.5F && f2 >= 0.5F) {
				this.playSound(SoundEvents.CHEST_CLOSE);
			}

			if (this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}
	}

	public float getLidAngle() {
		return lidAngle;
	}

	public void setLidAngle(float lidAngle) {
		this.lidAngle = lidAngle;
	}

}
