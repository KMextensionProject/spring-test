package sk.golddigger.core;

/**
 * Defines the contract for state update capability in this API.
 */
public interface Updatable {

	/**
	 * Updates the internal state of an implementor class.
	 */
	public void updateState();

}
