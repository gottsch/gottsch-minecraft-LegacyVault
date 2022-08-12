/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2021, Mark Gottschling (gottsch)
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
package mod.gottsch.forge.legacyvault.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import mod.gottsch.forge.legacyvault.LegacyVault;
import mod.gottsch.forge.legacyvault.block.entity.VaultBlockEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * 
 * @author Mark Gottschling on Aug 10, 2022
 *
 * @param <T>
 */
public class VaultModel extends Model implements IVaultModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(LegacyVault.MODID, "vault"), "main");
	
	private final ModelPart mainGroup;
	private final ModelPart doorGroup;
	private final ModelPart handle;
	private final ModelPart bolt;
	private float boltX;
	
	/**
	 * 
	 * @param root
	 */
	public VaultModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.mainGroup = root.getChild("mainGroup");
		this.doorGroup = root.getChild("doorGroup");
		this.handle = doorGroup.getChild("handle");
		this.bolt = doorGroup.getChild("slidingBolts");
		this.boltX = bolt.x;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition mainGroup = partdefinition.addOrReplaceChild("mainGroup", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -8.0F, -4.0F, 14.0F, 15.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(24, 30).addBox(-7.0F, -8.0F, -6.0F, 14.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(24, 26).addBox(-7.0F, 5.0F, -6.0F, 14.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 34).addBox(5.0F, -6.0F, -6.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(24, 34).addBox(-7.0F, -6.0F, -6.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 47).addBox(-6.0F, -5.0F, -7.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 44).addBox(-6.0F, 0.0F, -7.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 39).addBox(5.0F, -5.0F, -7.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition rivits = mainGroup.addOrReplaceChild("rivits", CubeListBuilder.create().texOffs(22, 26).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(22, 26).addBox(-0.5F, -0.5F, 12.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(40, 44).addBox(-0.5F, -13.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(40, 44).addBox(-0.5F, -13.5F, 12.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(45, 0).addBox(11.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(45, 0).addBox(11.3076F, -0.6924F, 12.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 45).addBox(11.5F, -13.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 45).addBox(11.5F, -13.5F, 12.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 6.0F, -6.0F));

		PartDefinition feet = mainGroup.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(0, 44).addBox(5.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 41).addBox(-7.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 41).addBox(5.0F, 7.0F, 5.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 41).addBox(-7.0F, 7.0F, 5.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition doorGroup = partdefinition.addOrReplaceChild("doorGroup", CubeListBuilder.create().texOffs(6, 0).addBox(8.0F, -5.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(8.0F, 1.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(0.0F, -6.0F, 0.0F, 10.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 36).addBox(-0.1F, -4.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(39, 8).addBox(-0.1F, 1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 34).addBox(5.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 16.0F, -6.0F));

		PartDefinition handle = doorGroup.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(20, 39).addBox(-0.5F, -3.5F, -1.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 39).addBox(-3.5F, -0.5F, -1.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.5F, -0.5F, -1.0F));


		PartDefinition slidingBolts = doorGroup.addOrReplaceChild("slidingBolts", CubeListBuilder.create().texOffs(40, 39).addBox(7.5F, -4.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(7.5F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		mainGroup.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		doorGroup.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	/**
	 * Perform animation modifications to the model.
	 * @param vaultBlockEntity
	 * @param partialTicks
	 */
	public void setupAnim(VaultBlockEntity entity, float partialTicks) {
		// move the rotation calculations to here from entity.
		// ie set y-rotation etc
		if (entity.isLidClosed()) {
			// spin the handle on the z-axis
			float handleRotation = entity.getPrevHandleAngle() + (entity.getHandleAngle() - entity.getPrevHandleAngle()) * partialTicks;
			handleRotation = 1.0F - handleRotation;
			handleRotation = 1.0F - handleRotation * handleRotation * handleRotation;
			// TODO the handle angle modifier may be slower/faster than lid
			handle.zRot = (handleRotation * (float)Math.PI / /*getAngleModifier()*/ 2.0F);
			
			// update the model's bolt(s) position
			// NOTE:  TE boltPosition is the total amount of movement - not the exact position
			// therefor the x value needs to add the value of boltPosition, not replace it.
			bolt.x = getBoltStartingX() + entity.getBoltPosition();
		}
		float lidRotation = entity.getPrevLidAngle() + (entity.getLidAngle() - entity.getPrevLidAngle()) * partialTicks;
		lidRotation = 1.0F - lidRotation;
		lidRotation = 1.0F - lidRotation * lidRotation * lidRotation;
		doorGroup.yRot = (lidRotation * (float)Math.PI / /*getAngleModifier()*/ 2.0F);
	}

	public float getBoltStartingX() {
		return boltX;
	}
		
}
