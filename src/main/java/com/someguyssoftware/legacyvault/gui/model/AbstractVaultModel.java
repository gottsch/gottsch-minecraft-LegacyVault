/**
 * 
 */
package com.someguyssoftware.legacyvault.gui.model;

import java.util.function.Function;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.someguyssoftware.legacyvault.LegacyVault;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author Mark Gottschling on Apr 30, 2021
 *
 */
public abstract class AbstractVaultModel extends Model implements IVaultModel {

	public AbstractVaultModel(Function<ResourceLocation, RenderType> renderTypeIn) {
		super(renderTypeIn);
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		LegacyVault.LOGGER.info("THIS SHOULD NEVER BE CALLED");
	}
	
	@Override
	public final RenderType getChestRenderType(ResourceLocation location) {
		return super.renderType(location);
	}
	
	protected void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}
}
