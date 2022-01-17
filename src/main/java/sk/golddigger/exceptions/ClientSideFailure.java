package sk.golddigger.exceptions;

public class ClientSideFailure extends GeneralException {

	private static final long serialVersionUID = 5185023708885707900L;

	public ClientSideFailure(String message, Object... messageArguments) {
		super(message, messageArguments);
	}

}
