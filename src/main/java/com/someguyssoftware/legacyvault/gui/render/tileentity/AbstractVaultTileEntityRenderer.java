/**
 * 
 */
package com.someguyssoftware.legacyvault.gui.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.block.VaultBlock;
import com.someguyssoftware.legacyvault.gui.model.IVaultModel;
import com.someguyssoftware.legacyvault.gui.model.VaultModel;
import com.someguyssoftware.legacyvault.tileentity.AbstractVaultTileEntity;
import com.someguyssoftware.legacyvault.tileentity.IVaultTileEntity;
import com.someguyssoftware.legacyvault.tileentity.MediumVaultTileEntity;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

/**
 * 
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public abstract class AbstractVaultTileEntityRenderer<T extends AbstractVaultTileEntity> extends TileEntityRenderer<T> {
	private ResourceLocation texture;
	private IVaultModel model;

	/**
	 * 
	 * @param tileEntityRendererDispatcher
	 */
	public AbstractVaultTileEntityRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
		setTexture(new ResourceLocation(LegacyVault.MODID + ":textures/entity/vault/vault.png"));
		setModel(new VaultModel());
	}
	
	/**
	 * render the tile entity - called every frame while the tileentity is in view of the player
	 *
	 * @param tileEntityIn the associated tile entity
	 * @param partialTicks    the fraction of a tick that this frame is being rendered at - used to interpolate frames between
	 *                        ticks, to make animations smoother.  For example - if the frame rate is steady at 80 frames per second,
	 *                        this method will be called four times per tick, with partialTicks spaced 0.25 apart, (eg) 0, 0.25, 0.5, 0.75
	 * @param matrixStack     the matrixStack is used to track the current view transformations that have been applied - i.e translation, rotation, scaling
	 *                        it is needed for you to render the view properly.
	 * @param renderBuffers    the buffer that you should render your model to
	 * @param combinedLight   the blocklight + skylight value for the tileEntity.  see http://greyminecraftcoder.blogspot.com/2014/12/lighting-18.html (outdated, but the concepts are still valid)
	 * @param combinedOverlay value for the "combined overlay" which changes the render based on an overlay texture (see OverlayTexture class).
	 *                        Used by vanilla for (1) red tint when a living entity takes damage, and (2) "flash" effect for creeper when ignited
	 *                        CreeperRenderer.getOverlayProgress()
	 */
	@Override
	public void render(T tileEntity, float partialTicks, MatrixStack matrixStack,
			IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {

		if (!(tileEntity instanceof IVaultTileEntity)) {
			return; // should never happen
		}

		World world = tileEntity.getLevel();
		boolean hasWorld = (world != null);
		BlockState state = tileEntity.getBlockState();
		Direction facing = Direction.NORTH;
		if (hasWorld) {
			facing = state.getValue(VaultBlock.FACING);
		}

		// push the current transformation matrix + normals matrix
		matrixStack.pushPose(); 

		// The model is defined centred on [0,0,0], so if we drew it at the current render origin, its centre would be
		// at the corner of the block, sunk halfway into the ground and overlapping into the adjacent blocks.
		// We want it to hover above the centre of the hopper base, so we need to translate up and across to the desired position
		final Vector3d TRANSLATION_OFFSET = new Vector3d(0.5, 1.5, 0.5);
		matrixStack.translate(TRANSLATION_OFFSET.x, TRANSLATION_OFFSET.y, TRANSLATION_OFFSET.z); // translate
		matrixStack.scale(-1, -1, 1);
		float f = getHorizontalAngle(facing);
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(-f));
		
		// update the lid rotation
		updateModelRotationAngles(tileEntity, partialTicks);

		IVertexBuilder renderBuffer = renderTypeBuffer.getBuffer(model.getChestRenderType(getTexture()));
		model.renderAll(matrixStack, renderBuffer, combinedLight, combinedOverlay, tileEntity);
		matrixStack.popPose();		

	}

	/**
	 * 
	 * @param tileEntity
	 * @param partialTicks
	 */
	public void updateModelRotationAngles(T tileEntity, float partialTicks) {
		if (tileEntity.isLidClosed()) {
			// spin the handle on the z-axis
			float handleRotation = tileEntity.getPrevHandleAngle() + (tileEntity.getHandleAngle() - tileEntity.getPrevHandleAngle()) * partialTicks;
			handleRotation = 1.0F - handleRotation;
			handleRotation = 1.0F - handleRotation * handleRotation * handleRotation;
			// TODO the handle angle modifier may be slower/faster than lid
			((VaultModel)getModel()).getHandle1().zRot = (handleRotation * (float)Math.PI / getAngleModifier());
			
			// update the model's bolt(s) position
			// NOTE:  TE boltPosition is the total amount of movement - not the exact position
			// therefor the x value needs to add the value of boltPosition, not replace it.
			((VaultModel)getModel()).getBolt().x = ((VaultModel)getModel()).getBoltStartingX() + tileEntity.getBoltPosition();
		}
		else {
			// render handleB rotating around y-axis (opening with rest of lid)
		}
		float lidRotation = tileEntity.getPrevLidAngle() + (tileEntity.getLidAngle() - tileEntity.getPrevLidAngle()) * partialTicks;
		lidRotation = 1.0F - lidRotation;
		lidRotation = 1.0F - lidRotation * lidRotation * lidRotation;
		getModel().getLid().yRot = (lidRotation * (float)Math.PI / getAngleModifier());
	}
	/**
	 * 
	 * @param tileEntity
	 * @param partialTicks
	 */
//	public void updateModelRotationAngles(T tileEntity, float partialTicks) {
//		float lidRotation = tileEntity.getPrevLidAngle() + (tileEntity.getLidAngle() - tileEntity.getPrevLidAngle()) * partialTicks;
//		lidRotation = 1.0F - lidRotation;
//		lidRotation = 1.0F - lidRotation * lidRotation * lidRotation;
//		model.getLid().xRot = -(lidRotation * (float) Math.PI / getAngleModifier());
//	}

	/**
	 * Modifies the max angle that the lid can swing.
	 * The max swing angle by default is 180 degrees. The max swing angle is divided the modifier.
	 * Increasing the size of the modifier reduces the size of the max swing angle.
	 * Ex:
	 * Return 2.0 = 90 degrees
	 * Return 3.0 = 60 degrees
	 * Return 4.0 = 45 degrees
	 * 
	 * @return
	 */
	public float getAngleModifier() {
		return 2.0F;
	}
	
	/**
	 * Helper method since all my models face the opposite direction of vanilla models
	 * @param meta
	 * @return
	 */
	public int getHorizontalAngle(Direction facing) {
		switch (facing) {
		default:
		case NORTH:
			return 0;
		case SOUTH:
			return 180;
		case WEST:
			return 90;
		case EAST:
			return -90;
		}
	}

	/**
	 * @return the texture
	 */
	public ResourceLocation getTexture() {
		return texture;
	}

	/**
	 * @param texture
	 *            the texture to set
	 */
	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
	}

	/**
	 * @return the model
	 */
	public IVaultModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(IVaultModel model) {
		this.model = model;
	}

}
