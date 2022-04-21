package common.messages;

public class UserListMessage extends Message {
	
	private static final long serialVersionUID = 1L;

	public UserListMessage(String origin, String destination) {
		super(MessageType.USER_LIST, origin, destination);
	}

}
