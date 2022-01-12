package sk.exceptions;

import sk.abstract_interface.MessageResolver;

public class GeneralException extends RuntimeException {

	private static final long serialVersionUID = -50378626526532631L;

	private final String message;
	
	public GeneralException (String message, Object... messageArguments) {
		super();
		this.message = MessageResolver.resolveMessage(message, messageArguments);
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
