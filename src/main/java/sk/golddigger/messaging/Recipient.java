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

	public boolean isDefined() {
		boolean emailPresent = (email != null && !email.equals("null"));
		boolean phonePresent = (phoneNumber != null && !phoneNumber.equals("null"));

		return emailPresent || phonePresent;
	}

	@Override
	public String toString() {
		return "name: " + this.name + ", email: " + this.email + ", phone: " + this.phoneNumber;
	}
}
