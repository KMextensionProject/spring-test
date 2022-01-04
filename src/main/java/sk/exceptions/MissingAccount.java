package sk.exceptions;

public class MissingAccount extends GeneralException {

	private static final long serialVersionUID = 1682452594700055279L;

	public MissingAccount(String message, Object... messageArguments) {
		super(message, messageArguments);
	}

}
