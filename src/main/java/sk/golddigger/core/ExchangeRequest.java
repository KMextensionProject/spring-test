package sk.golddigger.core;

import java.util.List;
import java.util.Map;

/**
 * kontrakt pre ziskanie dat exchange uctu/aktualneho stavu/ transakcii
 *
 */
public interface ExchangeRequest {

	public List<Map<String, Object>> getAllAccounts();

	public List<Map<String, Object>> getAllOrderFills();

	public double getAccountBalance(String accountId);

	public String postOrder(Order order);
}
