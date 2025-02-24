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
package mod.gottsch.forge.legacyvault.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import mod.gottsch.forge.legacyvault.core.block.RusticVaultBlock;
import mod.gottsch.forge.legacyvault.core.block.entity.IVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.block.entity.RusticVaultBlockEntity;
import mod.gottsch.forge.legacyvault.core.client.model.RusticVaultModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * 
 * @author Mark Gottschling on Feb 18, 2025
 *
 */
public class RusticVaultRenderer implements BlockEntityRenderer<RusticVaultBlockEntity> {

	/*
	 * NOTE when defining a resource location for the Atlas, you don't need to specify the /textures/ parent folder nor, the .png extension
	 */
	public static final ResourceLocation RUSTIC_VAULT_RENDERER_ATLAS_TEXTURE = new ResourceLocation(LegacyVault.MOD_ID, "entity/vault/rustic_vault");

	private Material material;
	private RusticVaultModel vaultModel;

	/**
	 *
	 * @param context
	 */
	public RusticVaultRenderer(BlockEntityRendererProvider.Context context) {
		this.vaultModel = new RusticVaultModel(context.bakeLayer(RusticVaultModel.LAYER_LOCATION));
		material = new Material(TextureAtlas.LOCATION_BLOCKS, RUSTIC_VAULT_RENDERER_ATLAS_TEXTURE);
	}
	
	@Override
	public void render(RusticVaultBlockEntity vaultBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
					   int combinedLight, int combinedOverlay) {

		if (!(vaultBlockEntity instanceof IVaultBlockEntity)) {
			return; // should never happen
		}
		
		Level world = vaultBlockEntity.getLevel();
		boolean hasWorld = (world != null);
		BlockState state = vaultBlockEntity.getBlockState();
		Direction facing = Direction.NORTH;
		if (hasWorld) {
			facing = state.getValue(RusticVaultBlock.FACING);
		}

		// Always remember to push the current transformation so that you can restore it later
        poseStack.pushPose();
        
		// The model is defined centred on [0,0,0], so if we drew it at the current render origin, its centre would be
		// at the corner of the block, sunk halfway into the ground and overlapping into the adjacent blocks.
		// We want it to hover above the centre of the hopper base, so we need to translate up and across to the desired position
		final Vec3 TRANSLATION_OFFSET = new Vec3(0.5, 1.5, 0.5);
		poseStack.translate(TRANSLATION_OFFSET.x, TRANSLATION_OFFSET.y, TRANSLATION_OFFSET.z); // translate
		
		poseStack.scale(-1, -1, 1);
		float f = getHorizontalAngle(facing);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(-f));
		
		// update the lid rotation - animation
		this.vaultModel.setupAnim(vaultBlockEntity, partialTicks);
		
		VertexConsumer renderBuffer = material.buffer(bufferSource, RenderType::entitySolid);		
		vaultModel.renderToBuffer(poseStack, renderBuffer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
	}

	/**
	 * Helper method since all my models face the opposite direction of vanilla models
	 *
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
}
