/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021 Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.block.VaultBlock;
import mod.gottsch.forge.legacyvault.block.entity.IVaultBlockEntity;
import mod.gottsch.forge.legacyvault.block.entity.VaultBlockEntity;
import mod.gottsch.forge.legacyvault.client.model.VaultModel;
import mod.gottsch.forge.legacyvault.setup.Registration;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 
 * @author Mark Gottschling on Aug 9, 2022
 *
 */
public class _VaultRenderer_for_EntityModel implements BlockEntityRenderer<VaultBlockEntity>{
	
	/*
	 * NOTE when defining a resource location for the Atlas, you don't need to specify the /textures/ parent folder nor, the .png extension
	 */
	public static final ResourceLocation VAULT_RENDERER_ATLAS_TEXTURE = new ResourceLocation(LegacyVault.MODID, "entity/vault/vault");

	private ModelPart vaultModel;
	private Material material;
	
	// method 2 - model
	private VaultModel vm;
	
	/**
	 * 
	 * @param context
	 */
	public _VaultRenderer_for_EntityModel(BlockEntityRendererProvider.Context context) {
		this.vm = new VaultModel(context.bakeLayer(VaultModel.LAYER_LOCATION));
		vaultModel = context.bakeLayer(VaultModel.LAYER_LOCATION);
		material = new Material(TextureAtlas.LOCATION_BLOCKS, VAULT_RENDERER_ATLAS_TEXTURE);
	}
	
	@Override
	public void render(VaultBlockEntity vaultBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
			int combinedLight, int combinedOverlay) {

		if (!(vaultBlockEntity instanceof IVaultBlockEntity)) {
			return; // should never happen
		}
		
		Level world = vaultBlockEntity.getLevel();
		boolean hasWorld = (world != null);
		BlockState state = vaultBlockEntity.getBlockState();
		Direction facing = Direction.NORTH;
		if (hasWorld) {
			facing = state.getValue(VaultBlock.FACING);
		}
		
		// Always remember to push the current transformation so that you can restore it later
        poseStack.pushPose();
        
		// The model is defined centred on [0,0,0], so if we drew it at the current render origin, its centre would be
		// at the corner of the block, sunk halfway into the ground and overlapping into the adjacent blocks.
		// We want it to hover above the centre of the hopper base, so we need to translate up and across to the desired position
		final Vector3d TRANSLATION_OFFSET = new Vector3d(0.5, 1.5, 0.5);
		poseStack.translate(TRANSLATION_OFFSET.x, TRANSLATION_OFFSET.y, TRANSLATION_OFFSET.z); // translate
		
		poseStack.scale(-1, -1, 1);
		float f = getHorizontalAngle(facing);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(-f));

		VertexConsumer renderBuffer = material.buffer(bufferSource, RenderType::entitySolid);		
        vaultModel.render(poseStack, renderBuffer, combinedLight, combinedOverlay);		

        poseStack.popPose();
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
}
