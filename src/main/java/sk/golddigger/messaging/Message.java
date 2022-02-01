package sk.golddigger.messaging;

public class Message {

	private String subject;
	private String body;

	public Message(String subject, String body) {
		this.subject = subject;
		this.body = body;
	}

	public Message(String body) {
		this(null, body);
	}

	public String getSubject() {
		return this.subject;
	}

	public String getBody() {
		return this.body;
	}
}
