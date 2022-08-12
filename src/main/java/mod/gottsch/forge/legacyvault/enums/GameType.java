/**
 * 
 */
package mod.gottsch.forge.legacyvault.enums;

/**
 * @author Mark Gottschling on May 2, 2021
 *
 */
public enum GameType {
	NORMAL("normal"),
	HARDCORE("hardcore");
	
	String value;
	
	GameType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
