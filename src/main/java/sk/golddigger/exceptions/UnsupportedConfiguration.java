package sk.golddigger.exceptions;

@SuppressWarnings("serial")
public class UnsupportedConfiguration extends GeneralException {

	public UnsupportedConfiguration(String messageCode, Object... messageArguments) {
		super(messageCode, messageArguments);
	}

}
