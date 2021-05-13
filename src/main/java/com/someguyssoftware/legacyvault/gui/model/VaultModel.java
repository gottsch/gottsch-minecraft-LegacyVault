package com.someguyssoftware.legacyvault.gui.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;

/*
 *  Made with Blockbench 3.8.4
 *  Exported for Minecraft version 1.15 - 1.16
 */
public class VaultModel extends AbstractVaultModel {
	private final ModelRenderer mainGroup;
	private final ModelRenderer rivit4;
	private final ModelRenderer rivit8;
	private final ModelRenderer rivit3;
	private final ModelRenderer rivit7;
	private final ModelRenderer rivit2;
	private final ModelRenderer rivit6;
	private final ModelRenderer rivit1;
	private final ModelRenderer rivit5;
	private final ModelRenderer feet;
	private final ModelRenderer doorGroup;
	private final ModelRenderer handle;
	private final ModelRenderer handle2;
	private final ModelRenderer slidingBolts;
	private final ModelRenderer rotatingBolts;

	private float boltX;
	
	/**
	 * 
	 */
	public VaultModel() {
		super(RenderType::entityCutout);
		texWidth = 64;
		texHeight = 64;

		mainGroup = new ModelRenderer(this);
		mainGroup.setPos(0.0F, 16.0F, 0.0F);
		mainGroup.texOffs(0, 0).addBox(-7.0F, -8.0F, -4.0F, 14.0F, 15.0F, 11.0F, 0.0F, false);
		mainGroup.texOffs(24, 30).addBox(-7.0F, -8.0F, -6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
		mainGroup.texOffs(24, 26).addBox(-7.0F, 5.0F, -6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
		mainGroup.texOffs(32, 34).addBox(5.0F, -6.0F, -6.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);
		mainGroup.texOffs(24, 34).addBox(-7.0F, -6.0F, -6.0F, 2.0F, 11.0F, 2.0F, 0.0F, false);
		mainGroup.texOffs(0, 47).addBox(-6.0F, -5.0F, -7.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		mainGroup.texOffs(12, 44).addBox(-6.0F, 0.0F, -7.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		mainGroup.texOffs(16, 39).addBox(5.0F, -5.0F, -7.0F, 1.0F, 9.0F, 1.0F, 0.0F, false);

		rivit4 = new ModelRenderer(this);
		rivit4.setPos(-6.0F, 6.0F, -6.0F);
		mainGroup.addChild(rivit4);
		setRotation(rivit4, 0.0F, 0.0F, 0.7854F);
		rivit4.texOffs(22, 26).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		rivit8 = new ModelRenderer(this);
		rivit8.setPos(-6.0F, 6.0F, -6.0F);
		mainGroup.addChild(rivit8);
		setRotation(rivit8, 0.0F, 0.0F, 0.7854F);
		rivit8.texOffs(22, 26).addBox(-0.5F, -0.5F, 12.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		rivit3 = new ModelRenderer(this);
		rivit3.setPos(-6.0F, -7.0F, -6.0F);
		mainGroup.addChild(rivit3);
		setRotation(rivit3, 0.0F, 0.0F, 0.7854F);
		rivit3.texOffs(40, 44).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		rivit7 = new ModelRenderer(this);
		rivit7.setPos(-6.0F, -7.0F, -6.0F);
		mainGroup.addChild(rivit7);
		setRotation(rivit7, 0.0F, 0.0F, 0.7854F);
		rivit7.texOffs(40, 44).addBox(-0.5F, -0.5F, 12.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		rivit2 = new ModelRenderer(this);
		rivit2.setPos(6.0F, 6.0F, -6.0F);
		mainGroup.addChild(rivit2);
		setRotation(rivit2, 0.0F, 0.0F, 0.7854F);
		rivit2.texOffs(45, 0).addBox(-0.6924F, -0.6924F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		rivit6 = new ModelRenderer(this);
		rivit6.setPos(6.0F, 6.0F, -6.0F);
		mainGroup.addChild(rivit6);
		setRotation(rivit6, 0.0F, 0.0F, 0.7854F);
		rivit6.texOffs(45, 0).addBox(-0.6924F, -0.6924F, 12.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		rivit1 = new ModelRenderer(this);
		rivit1.setPos(6.0F, -7.0F, -6.0F);
		mainGroup.addChild(rivit1);
		setRotation(rivit1, 0.0F, 0.0F, 0.7854F);
		rivit1.texOffs(44, 45).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		rivit5 = new ModelRenderer(this);
		rivit5.setPos(6.0F, -7.0F, -6.0F);
		mainGroup.addChild(rivit5);
		setRotation(rivit5, 0.0F, 0.0F, 0.7854F);
		rivit5.texOffs(44, 45).addBox(-0.5F, -0.5F, 12.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		feet = new ModelRenderer(this);
		feet.setPos(0.0F, 16.0F, 0.0F);
		feet.texOffs(0, 44).addBox(5.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		feet.texOffs(40, 41).addBox(-7.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		feet.texOffs(8, 41).addBox(5.0F, 7.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		feet.texOffs(0, 41).addBox(-7.0F, 7.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		doorGroup = new ModelRenderer(this);
		doorGroup.setPos(-5.0F, 16.0F, -6.0F);
		doorGroup.texOffs(6, 0).addBox(8.0F, -5.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		doorGroup.texOffs(0, 0).addBox(8.0F, 1.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		doorGroup.texOffs(0, 26).addBox(0.0F, -6.0F, 0.0F, 10.0F, 11.0F, 2.0F, 0.0F, false);
		doorGroup.texOffs(40, 36).addBox(-0.1F, -4.0F, -0.5F, 3.0F, 2.0F, 1.0F, 0.0F, false);
		doorGroup.texOffs(39, 8).addBox(-0.1F, 1.0F, -0.5F, 3.0F, 2.0F, 1.0F, 0.0F, false);
		doorGroup.texOffs(30, 34).addBox(4.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		handle = new ModelRenderer(this);
		handle.setPos(-1.5F, 15.5F, -8.0F);
		handle.texOffs(20, 39).addBox(0.5F, -3.5F, 0.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);
		handle.texOffs(0, 39).addBox(-2.5F, -0.5F, 0.0F, 7.0F, 1.0F, 1.0F, 0.0F, false);

		handle2 = new ModelRenderer(this);
		handle2.setPos(-5.0F, 16.0F, -6.0F);
		handle2.texOffs(8, 44).addBox(4.0F, -4.0F, -2.0F, 1.0F, 7.0F, 1.0F, 0.0F, false);
		handle2.texOffs(38, 34).addBox(1.0F, -1.0F, -2.0F, 7.0F, 1.0F, 1.0F, 0.0F, false);

		slidingBolts = new ModelRenderer(this);
		slidingBolts.setPos(-5.0F, 16.0F, -6.0F);
		slidingBolts.texOffs(40, 39).addBox(7.5F, -4.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		slidingBolts.texOffs(0, 8).addBox(7.5F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);

		rotatingBolts = new ModelRenderer(this);
		rotatingBolts.setPos(-5.0F, 16.0F, -6.0F);
		rotatingBolts.texOffs(40, 39).addBox(5.5F, -4.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		rotatingBolts.texOffs(0, 8).addBox(5.5F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);


		this.boltX = slidingBolts.x;
	}

	@Override
	public void renderAll(MatrixStack matrixStack, IVertexBuilder renderBuffer, int combinedLight, int combinedOverlay, VaultTileEntity te) {
		
		if (te.isHandleOpen && !te.isLidClosed) {
			handle2.yRot = doorGroup.yRot;
			handle2.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);
			rotatingBolts.yRot = doorGroup.yRot;
			rotatingBolts.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);
		}
		else if (te.isLidClosed) {		
			// rotate the handle
			handle.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);

			slidingBolts.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);
		}
//		topBolt.yRot = doorGroup.yRot;
//		bottomBolt.yRot = topBolt.yRot;
		
		mainGroup.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);
		feet.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);
		doorGroup.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);
//		topBolt.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);
//		bottomBolt.render(matrixStack, renderBuffer, combinedLight, combinedOverlay);
	}

	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

	@Override
	public ModelRenderer getLid() {
		return doorGroup;
	}

	public ModelRenderer getHandle1() {
		return handle;
	}
	
	public ModelRenderer getBolt() {
		return slidingBolts;
	}

	public float getBoltStartingX() {
		return boltX;
	}
}