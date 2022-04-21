package common.messages;

public class ErrorMessage extends Message {
	
	private static final long serialVersionUID = 1L;
	private String message;

	public ErrorMessage(String origin, String destination, String message) {
		super(MessageType.ERROR, origin, destination);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}
