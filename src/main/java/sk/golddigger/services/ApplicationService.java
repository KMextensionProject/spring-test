package sk.golddigger.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.golddigger.config.EndpointLoader;

@Service
public class ApplicationService {

	@Autowired
	private EndpointLoader endpointLoader;

	public String ping() {
		return "Status OK";
	}

	public final Set<String> getAvailableEndpoints() {
		return endpointLoader.getApplicationEndpoints();
	}

}
