package sk.golddigger.core;

/**
 * schopnost triedy aktualizovat ich stav v tomto api
 *
 */
public interface Refreshable {

	public void updateState() throws Exception;

}
