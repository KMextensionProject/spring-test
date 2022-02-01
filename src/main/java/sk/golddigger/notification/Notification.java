package sk.golddigger.notification;

import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;

public interface Notification {

	public void send(Message message, Recipient recipient);

}
