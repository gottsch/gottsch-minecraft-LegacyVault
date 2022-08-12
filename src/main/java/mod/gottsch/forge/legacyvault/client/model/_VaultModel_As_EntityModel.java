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
public class _VaultModel_As_EntityModel /*<T extends Entity> extends EntityModel<T>*/ extends Model implements IVaultModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(LegacyVault.MODID, "vault"), "main");
	
	private final ModelPart mainGroup;
	private final ModelPart feet;
	private final ModelPart doorGroup;
	private final ModelPart handle;
	private final ModelPart handle2;
	private final ModelPart slidingBolts;
	private final ModelPart rotatingBolts;

	public _VaultModel_As_EntityModel(ModelPart root) {
		// method 2
		super(RenderType::entitySolid);
		this.mainGroup = root.getChild("mainGroup");
		this.feet = root.getChild("feet");
		this.doorGroup = root.getChild("doorGroup");
		this.handle = root.getChild("handle");
		this.handle2 = root.getChild("handle2");
		this.slidingBolts = root.getChild("slidingBolts");
		this.rotatingBolts = root.getChild("rotatingBolts");
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

		PartDefinition rivit4 = mainGroup.addOrReplaceChild("rivit4", CubeListBuilder.create().texOffs(22, 26).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 6.0F, -6.0F));

		PartDefinition rivit8 = mainGroup.addOrReplaceChild("rivit8", CubeListBuilder.create().texOffs(22, 26).addBox(-0.5F, -0.5F, 12.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 6.0F, -6.0F));

		PartDefinition rivit3 = mainGroup.addOrReplaceChild("rivit3", CubeListBuilder.create().texOffs(40, 44).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -7.0F, -6.0F));

		PartDefinition rivit7 = mainGroup.addOrReplaceChild("rivit7", CubeListBuilder.create().texOffs(40, 44).addBox(-0.5F, -0.5F, 12.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, -7.0F, -6.0F));

		PartDefinition rivit2 = mainGroup.addOrReplaceChild("rivit2", CubeListBuilder.create().texOffs(45, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 6.0F, -6.0F));

		PartDefinition rivit6 = mainGroup.addOrReplaceChild("rivit6", CubeListBuilder.create().texOffs(45, 0).addBox(-0.6924F, -0.6924F, 12.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 6.0F, -6.0F));

		PartDefinition rivit1 = mainGroup.addOrReplaceChild("rivit1", CubeListBuilder.create().texOffs(44, 45).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -7.0F, -6.0F));

		PartDefinition rivit5 = mainGroup.addOrReplaceChild("rivit5", CubeListBuilder.create().texOffs(44, 45).addBox(-0.5F, -0.5F, 12.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -7.0F, -6.0F));

		PartDefinition feet = partdefinition.addOrReplaceChild("feet", CubeListBuilder.create().texOffs(0, 44).addBox(5.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 41).addBox(-7.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 41).addBox(5.0F, 7.0F, 5.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 41).addBox(-7.0F, 7.0F, 5.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition doorGroup = partdefinition.addOrReplaceChild("doorGroup", CubeListBuilder.create().texOffs(6, 0).addBox(8.0F, -5.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(8.0F, 1.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 26).addBox(0.0F, -6.0F, 0.0F, 10.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 36).addBox(-0.1F, -4.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(39, 8).addBox(-0.1F, 1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 34).addBox(4.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 16.0F, -6.0F));

		PartDefinition handle = partdefinition.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(20, 39).addBox(1.5F, -3.5F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 39).addBox(-1.5F, -0.5F, 0.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 15.5F, -8.0F));

		PartDefinition handle2 = partdefinition.addOrReplaceChild("handle2", CubeListBuilder.create().texOffs(8, 44).addBox(5.0F, -4.0F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(38, 34).addBox(2.0F, -1.0F, -2.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 16.0F, -6.0F));

		PartDefinition slidingBolts = partdefinition.addOrReplaceChild("slidingBolts", CubeListBuilder.create().texOffs(40, 39).addBox(7.5F, -4.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(7.5F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 16.0F, -6.0F));

		PartDefinition rotatingBolts = partdefinition.addOrReplaceChild("rotatingBolts", CubeListBuilder.create().texOffs(40, 39).addBox(5.5F, -4.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 8).addBox(5.5F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 16.0F, -6.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

//	@Override
//	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//
//	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		mainGroup.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		feet.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		doorGroup.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		handle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		handle2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		slidingBolts.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		rotatingBolts.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(VaultBlockEntity vaultBlockEntity, float f, float g, float h, float i) {
		// TODO Auto-generated method stub
		
	}
	
	
}
