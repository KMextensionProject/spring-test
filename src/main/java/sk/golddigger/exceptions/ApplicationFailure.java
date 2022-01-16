package sk.golddigger.exceptions;

public class ApplicationFailure extends GeneralException {

	private static final long serialVersionUID = -7578145761497666458L;

	public ApplicationFailure(String message, Object... messageArguments) {
		super(message, messageArguments);
	}
}
