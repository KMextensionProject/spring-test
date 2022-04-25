package sk.golddigger.messaging;

public interface SMSSender {

	/**
	 * Safely send SMS message.
	 * @return true if the message was sent successfully, false otherwise;
	 */
	public abstract boolean send(Message message, Recipient recipient);

}
