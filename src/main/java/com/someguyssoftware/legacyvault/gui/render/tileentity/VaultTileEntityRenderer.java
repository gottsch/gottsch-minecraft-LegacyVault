package com.someguyssoftware.legacyvault.gui.render.tileentity;

import com.someguyssoftware.legacyvault.tileentity.AbstractVaultTileEntity;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// NOTE @OnlyIn extremely important! add to all Renderers
@OnlyIn(Dist.CLIENT)
public class VaultTileEntityRenderer<T extends AbstractVaultTileEntity> extends AbstractVaultTileEntityRenderer<T> {

	/**
	 * 
	 * @param tileEntityRendererDispatcher
	 */
	public VaultTileEntityRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
	}
}
