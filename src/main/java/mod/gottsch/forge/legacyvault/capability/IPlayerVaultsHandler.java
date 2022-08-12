/**
 * 
 */
package mod.gottsch.forge.legacyvault.capability;

import java.util.List;

import com.someguyssoftware.gottschcore.spatial.ICoords;

/**
 * @author Mark Gottschling on May 11, 2021
 *
 */
public interface IPlayerVaultsHandler {

	int getCount();

	void setCount(int size);

	List<ICoords> getLocations();

	void setLocations(List<ICoords> locations);

}
