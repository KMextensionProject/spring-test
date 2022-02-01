package sk.golddigger.messaging;

public class Recipient {

	private String name;
	private String email;
	private String phoneNumber;

	public Recipient withName(String name) {
		this.name = name;
		return this;
	}

	public Recipient withEmail(String email) {
		this.email = email;
		return this;
	}

	public Recipient withPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}
}
