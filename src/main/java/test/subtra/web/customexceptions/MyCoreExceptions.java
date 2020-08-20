package test.subtra.web.customexceptions;

public class MyCoreExceptions extends Exception {

	private static final long serialVersionUID = 7865939573625396301L;

	private String message = null;

	public MyCoreExceptions() {
		super();
	}

	public MyCoreExceptions(String message) {
		super(message);
		this.message = message;
	}

	public MyCoreExceptions(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
