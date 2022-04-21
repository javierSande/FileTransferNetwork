package common.messages;

public class TerminateMessage extends Message {
	
	private static final long serialVersionUID = 1L;

	public TerminateMessage(String origin, String destination) {
		super(MessageType.TERMINATE, origin, destination);
	}

}
