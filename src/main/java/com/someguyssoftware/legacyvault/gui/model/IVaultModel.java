/**
 * 
 */
package com.someguyssoftware.legacyvault.gui.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * 
 * @author Mark Gottschling on Apr 29, 2021
 *
 */
public interface IVaultModel {
	public ModelRenderer getLid();

	void renderAll(MatrixStack matrixStack, IVertexBuilder renderBuffer, int combinedLight, int combinedOverlay,
			VaultTileEntity te);
	
	/**
	 * wrapper for vanilla Model.getRenderType()
	 * @param locationIn
	 * @return
	 */
	public RenderType getChestRenderType(ResourceLocation location);
}
