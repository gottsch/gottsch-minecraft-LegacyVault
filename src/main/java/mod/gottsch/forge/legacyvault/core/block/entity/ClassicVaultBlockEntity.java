/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
 * @author Mark Gottschling on Jun 18, 2022
 *
 */
public class ClassicVaultBlockEntity extends AbstractVaultBlockEntity {

	/*
	 * Client updated variables
	 */
	/** The current angle of the lid (between 0 and 1) */
	protected float lidAngle;
	/** The angle of the lid last tick */
	protected float prevLidAngle;

	protected float handleAngle;
	protected float prevHandleAngle;

	private float boltPosition = 0F;
	protected float prevBoltPosition = 0F;

	protected boolean isHandleOpen = false;
	protected boolean isHandleClosed = true;
	protected boolean isBoltOpen = false;
	protected boolean isBoltClosed = false;
	protected boolean isLidOpen = false;
	protected boolean isLidClosed = false;

	/**
	 *
	 * @param pos
	 * @param state
	 */
	public ClassicVaultBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CLASSIC_VAULT.get(), pos, state);
	}

	public void tickClient() {
		// save the previous positions and angles of vault components
		this.prevLidAngle = this.lidAngle;
		this.prevHandleAngle = this.handleAngle;
		this.prevBoltPosition = this.getBoltPosition();

		// opening ie. players
		if (this.openCount > 0) {
			// test the handle
			if (this.handleAngle > -1.0F) {
				isHandleOpen = false;
				isHandleClosed = false;

				this.handleAngle -= 0.1F;
				if (this.handleAngle <= -1.0F) {
					this.handleAngle = -1.0F;
					isHandleOpen = true;
				}
			}
			else {
				isHandleOpen = true;
			}
			if (this.getBoltPosition() > -2.0F) {
				// NOTE doesn't require isBoltOpen test as it is in sync with handle (isHandleOpen)
				this.setBoltPosition(this.getBoltPosition() - 0.2F);
				if (this.getBoltPosition() <= -2.0F) {
					this.setBoltPosition(-2.0F);
				}
			}
			if (isHandleOpen) {
				// play the opening chest sound the at the beginning of opening
				if (this.lidAngle == 0.0F) {
						this.playSound(SoundEvents.CHEST_OPEN);
				}

				// test the lid
				if (this.lidAngle < 1.0F) {
					isLidOpen = false;
					isLidClosed = false;
					this.lidAngle += 0.1F;
					if (this.lidAngle >= 1.0F) {
						this.lidAngle = 1.0F;
						isLidOpen = true;
					}
				} else {
					isLidOpen = true;
				}
			}
		}
		// closing ie no players
		else {
			float f2 = this.lidAngle;

			if (this.lidAngle > 0.0F) {
				isLidClosed = false;
				isLidOpen = false;

				this.lidAngle -= 0.1F;
				if (this.lidAngle <= 0.0F) {
					this.lidAngle = 0.0F;
					isLidClosed = true;
				}
			} else {
				isLidClosed = true;
			}

			// play the closing sound
			if (this.lidAngle < 0.7F && f2 >= 0.7F) {
				this.playSound(SoundEvents.CHEST_CLOSE);
			}

			if (isLidClosed) {
				if (this.handleAngle < 0.0F) {
					isHandleClosed = false;
					isHandleOpen = false;

					this.handleAngle += 0.1F;
					if (this.handleAngle >= 0.0F) {
						this.handleAngle = 0.0F;
						isHandleClosed = true;
					}

					if (this.getBoltPosition() < 0F) {
						this.setBoltPosition(this.getBoltPosition() + 0.2F);
						if (this.getBoltPosition() >= 0F) {
							this.setBoltPosition(0F);
						}
					}
				} else {
					isHandleClosed = true;
				}
			}
		}
	}

	public boolean isHandleOpen() {
		return isHandleOpen;
	}

	public void setHandleOpen(boolean isHandleOpen) {
		this.isHandleOpen = isHandleOpen;
	}

	public boolean isHandleClosed() {
		return isHandleClosed;
	}

	public void setHandleClosed(boolean isHandleClosed) {
		this.isHandleClosed = isHandleClosed;
	}

	public boolean isLidOpen() {
		return isLidOpen;
	}

	public void setLidOpen(boolean isLidOpen) {
		this.isLidOpen = isLidOpen;
	}

	public boolean isLidClosed() {
		return isLidClosed;
	}

	public void setLidClosed(boolean isLidClosed) {
		this.isLidClosed = isLidClosed;
	}

	public float getBoltPosition() {
		return boltPosition;
	}

	public void setBoltPosition(float boltPosition) {
		this.boltPosition = boltPosition;
	}

	public float getPrevLidAngle() {
		return prevLidAngle;
	}

	public void setPrevLidAngle(float prevLidAngle) {
		this.prevLidAngle = prevLidAngle;
	}

	public float getPrevHandleAngle() {
		return prevHandleAngle;
	}

	public float getHandleAngle() {
		return handleAngle;
	}

	public void setHandleAngle(float handleAngle) {
		this.handleAngle = handleAngle;
	}

	public float getPrevBoltPosition() {
		return prevBoltPosition;
	}

	public void setPrevBoltPosition(float prevBoltPosition) {
		this.prevBoltPosition = prevBoltPosition;
	}

	public void setPrevHandleAngle(float prevHandleAngle) {
		this.prevHandleAngle = prevHandleAngle;
	}

	public float getLidAngle() {
		return lidAngle;
	}

	public void setLidAngle(float lidAngle) {
		this.lidAngle = lidAngle;
	}

}
