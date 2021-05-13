package com.someguyssoftware.legacyvault.gui.render.tileentity;

import com.someguyssoftware.legacyvault.LegacyVault;
import com.someguyssoftware.legacyvault.gui.model.VaultModel;
import com.someguyssoftware.legacyvault.tileentity.VaultTileEntity;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// TODO rename to VaultTileEntityRenderer once the other "chest" rendered is removed
// NOTE @OnlyIn extremely important! add to all Renderers
@OnlyIn(Dist.CLIENT)
public class SteamPunkVaultTileEntityRenderer extends AbstractLegacyVaultTileEntityRenderer {

	public SteamPunkVaultTileEntityRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
		super(tileEntityRendererDispatcher);
		setTexture(new ResourceLocation(LegacyVault.MODID + ":textures/entity/vault/vault.png"));
		setModel(new VaultModel());
	}

	@Override
	public void updateModelRotationAngles(VaultTileEntity tileEntity, float partialTicks) {
		if (tileEntity.isLidClosed) {
			// spin the handle on the z-axis
			float handleRotation = tileEntity.prevHandleAngle + (tileEntity.handleAngle - tileEntity.prevHandleAngle) * partialTicks;
			handleRotation = 1.0F - handleRotation;
			handleRotation = 1.0F - handleRotation * handleRotation * handleRotation;
			// TODO the handle angle modifier may be slower/faster than lid
			((VaultModel)getModel()).getHandle1().zRot = (handleRotation * (float)Math.PI / getAngleModifier());
			
			// update the model's bolt(s) position
			// NOTE:  TE boltPosition is the total amount of movement - not the exact position
			// therefor the x value needs to add the value of boltPosition, not replace it.
			((VaultModel)getModel()).getBolt().x = ((VaultModel)getModel()).getBoltStartingX() + tileEntity.boltPosition;
		}
		else {
			// render handleB rotating around y-axis (opening with rest of lid)
		}
		float lidRotation = tileEntity.prevLidAngle + (tileEntity.lidAngle - tileEntity.prevLidAngle) * partialTicks;
		lidRotation = 1.0F - lidRotation;
		lidRotation = 1.0F - lidRotation * lidRotation * lidRotation;
		getModel().getLid().yRot = (lidRotation * (float)Math.PI / getAngleModifier());
	}

}
