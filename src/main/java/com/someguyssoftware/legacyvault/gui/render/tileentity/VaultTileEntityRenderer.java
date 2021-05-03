package com.someguyssoftware.legacyvault.gui.render.tileentity;


import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.gui.model.StandardVaultModel;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// NOTE @OnlyIn extremely important! add to all Renderers
@OnlyIn(Dist.CLIENT)
public class VaultTileEntityRenderer extends AbstractLegacyVaultTileEntityRenderer {

	public VaultTileEntityRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);

		setTexture(new ResourceLocation(LegacyVault.MODID + ":textures/entity/chest/vault.png"));
		setModel(new StandardVaultModel());
	}

}
