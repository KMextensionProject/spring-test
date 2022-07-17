package sk.golddigger.messaging;

public class Recipient {

	private String name;
	private String email;
	private String phoneNumber;
	private String other;

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

	public Recipient withOtherAddress(String other) {
		this.other = other;
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

	public String getOtherAddress() {
		return this.other;
	}

	public boolean isDefined() {
		boolean emailPresent = (email != null && !email.equals("null"));
		boolean phonePresent = (phoneNumber != null && !phoneNumber.equals("null"));
		boolean otherAddressPresent = (other != null && !other.equals("null"));

		return emailPresent || phonePresent || otherAddressPresent;
	}

	@Override
	public String toString() {
		return "name: " + this.name + ", email: " + this.email + ", phone: " + this.phoneNumber;
	}
}
