package mod.gottsch.forge.legacyvault.exception;


/**
 * 
 * @author Mark Gottschling on Jan 18, 2018
 *
 */
public class DbInitializationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6357187077221278636L;

	public DbInitializationException() {
		super();
	}
	
	public DbInitializationException(String error) {
		super(error);
	}
	
	public DbInitializationException(Throwable throwable) {
		super(throwable);
	}
	
	public DbInitializationException(String error, Throwable throwable) {
		super(error, throwable);
	}
}
