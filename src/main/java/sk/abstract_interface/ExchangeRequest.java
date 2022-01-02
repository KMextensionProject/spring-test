package sk.abstract_interface;

import java.util.List;
import java.util.Map;

/**
 * kontrakt pre ziskanie dat exchange uctu/aktualneho stavu/ transakcii
 *
 */
public interface ExchangeRequest {

	public List<Map<String, Object>> getAllAccounts() throws Exception;

	public double getAccountBalance(String accountId) throws Exception;
}
