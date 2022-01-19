package sk.golddigger.services;

import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

	public String ping() {
		return "Status OK";
	}

}
