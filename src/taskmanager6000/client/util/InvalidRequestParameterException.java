package taskmanager6000.client.util;

public class InvalidRequestParameterException extends Exception{
	private static final long serialVersionUID = 1L;

	public InvalidRequestParameterException() {
		super();
	}
	
	public InvalidRequestParameterException(String message) {
		super(message);
	}
	
	public InvalidRequestParameterException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidRequestParameterException(Throwable cause) {
		super(cause);
	}
}
