/**
 * 
 */
package mod.gottsch.neo.legacyvault.core.capability;

import java.util.List;

import mod.gottsch.neo.gottschcore.spatial.DimensionCoords;


/**
 * @author Mark Gottschling on May 11, 2021
 *
 */
public interface IPlayerVaultsHandler {

	int getCount();

	void setCount(int size);

	List<DimensionCoords> getLocations();

	void setLocations(List<DimensionCoords> locations);

	int getVaultTier();

	void setVaultTier(int tier);

}
