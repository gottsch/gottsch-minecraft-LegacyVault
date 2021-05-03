// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


public class VaultBlock extends EntityModel<Entity> {
	private final ModelRenderer main;
	private final ModelRenderer rivit4_r1;
	private final ModelRenderer rivit3_r1;
	private final ModelRenderer rivit2_r1;
	private final ModelRenderer rivit1_r1;
	private final ModelRenderer feet;
	private final ModelRenderer door;
	private final ModelRenderer spoke;
	private final ModelRenderer gear;
	private final ModelRenderer g1_r1;

	public VaultBlock() {
		textureWidth = 16;
		textureHeight = 16;

		main = new ModelRenderer(this);
		main.setRotationPoint(0.0F, 16.0F, 0.0F);
		main.setTextureOffset(0, 11).addBox(-7.0F, -8.0F, -4.0F, 14.0F, 15.0F, 11.0F, 0.0F, false);
		main.setTextureOffset(0, 2).addBox(-7.0F, -8.0F, -6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
		main.setTextureOffset(0, 2).addBox(-7.0F, 5.0F, -6.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
		main.setTextureOffset(0, 2).addBox(5.0F, -6.0F, -6.0F, 2.0F, 13.0F, 2.0F, 0.0F, false);
		main.setTextureOffset(0, 2).addBox(-7.0F, -6.0F, -6.0F, 2.0F, 13.0F, 2.0F, 0.0F, false);
		main.setTextureOffset(0, 1).addBox(-6.0F, -5.0F, -7.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		main.setTextureOffset(0, 1).addBox(-6.0F, 0.0F, -7.0F, 1.0F, 4.0F, 1.0F, 0.0F, false);
		main.setTextureOffset(0, 1).addBox(2.0F, -8.0F, -7.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
		main.setTextureOffset(0, 1).addBox(1.0F, 5.0F, -7.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);

		rivit4_r1 = new ModelRenderer(this);
		rivit4_r1.setRotationPoint(-6.0F, 6.0F, -6.0F);
		main.addChild(rivit4_r1);
		setRotationAngle(rivit4_r1, 0.0F, 0.0F, 0.7854F);
		rivit4_r1.setTextureOffset(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);

		rivit3_r1 = new ModelRenderer(this);
		rivit3_r1.setRotationPoint(-6.0F, -7.0F, -6.0F);
		main.addChild(rivit3_r1);
		setRotationAngle(rivit3_r1, 0.0F, 0.0F, 0.7854F);
		rivit3_r1.setTextureOffset(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);

		rivit2_r1 = new ModelRenderer(this);
		rivit2_r1.setRotationPoint(6.0F, 6.0F, -6.0F);
		main.addChild(rivit2_r1);
		setRotationAngle(rivit2_r1, 0.0F, 0.0F, 0.7854F);
		rivit2_r1.setTextureOffset(0, 0).addBox(-0.6924F, -0.6924F, -0.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);

		rivit1_r1 = new ModelRenderer(this);
		rivit1_r1.setRotationPoint(6.0F, -7.0F, -6.0F);
		main.addChild(rivit1_r1);
		setRotationAngle(rivit1_r1, 0.0F, 0.0F, 0.7854F);
		rivit1_r1.setTextureOffset(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 0.0F, 0.0F, false);

		feet = new ModelRenderer(this);
		feet.setRotationPoint(0.0F, 16.0F, 0.0F);
		feet.setTextureOffset(0, 2).addBox(5.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		feet.setTextureOffset(0, 2).addBox(-7.0F, 7.0F, -6.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		feet.setTextureOffset(0, 2).addBox(5.0F, 7.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		feet.setTextureOffset(0, 2).addBox(-7.0F, 7.0F, 5.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		door = new ModelRenderer(this);
		door.setRotationPoint(0.0F, 16.0F, 0.0F);
		door.setTextureOffset(0, 2).addBox(-5.0F, -6.0F, -6.0F, 10.0F, 11.0F, 2.0F, 0.0F, false);
		door.setTextureOffset(0, 1).addBox(-5.0F, -4.1F, -6.5F, 5.0F, 2.0F, 1.0F, 0.0F, false);
		door.setTextureOffset(0, 1).addBox(-5.0F, 1.0F, -6.5F, 5.0F, 2.0F, 1.0F, 0.0F, false);
		door.setTextureOffset(0, 1).addBox(1.0F, -3.0F, -7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		door.setTextureOffset(0, 1).addBox(1.0F, 0.0F, -7.0F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		door.setTextureOffset(0, 1).addBox(3.0F, -7.9F, -6.5F, 1.0F, 6.0F, 1.0F, 0.0F, false);
		door.setTextureOffset(0, 1).addBox(2.0F, -1.0F, -6.5F, 1.0F, 6.0F, 1.0F, 0.0F, false);

		spoke = new ModelRenderer(this);
		spoke.setRotationPoint(0.0F, 0.0F, 0.0F);
		door.addChild(spoke);
		spoke.setTextureOffset(0, 1).addBox(-4.0F, -3.0F, -8.0F, 5.0F, 5.0F, 1.0F, 0.0F, false);
		spoke.setTextureOffset(0, 1).addBox(-2.0F, -1.0F, -7.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		gear = new ModelRenderer(this);
		gear.setRotationPoint(0.0F, 17.0F, -1.0F);
		gear.setTextureOffset(0, 4).addBox(6.7F, -3.0F, 0.0F, 0.0F, 4.0F, 4.0F, 0.0F, false);

		g1_r1 = new ModelRenderer(this);
		g1_r1.setRotationPoint(8.0F, -1.0F, 2.0F);
		gear.addChild(g1_r1);
		setRotationAngle(g1_r1, -0.7854F, 0.0F, 0.0F);
		g1_r1.setTextureOffset(0, 4).addBox(-1.5F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		main.render(matrixStack, buffer, packedLight, packedOverlay);
		feet.render(matrixStack, buffer, packedLight, packedOverlay);
		door.render(matrixStack, buffer, packedLight, packedOverlay);
		gear.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}