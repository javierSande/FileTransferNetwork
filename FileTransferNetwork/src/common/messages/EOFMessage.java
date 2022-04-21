package common.messages;

public class EOFMessage extends Message {

	private static final long serialVersionUID = 1L;

	public EOFMessage(String origin, String destination) {
		super(MessageType.EOF, origin, destination);
	}

}
