package common.messages;

import common.User;

public class SendRequestMessage extends Message {
	
	private static final long serialVersionUID = 1L;
	private User receiver;
	private String file;

	public SendRequestMessage(String origin, String destination, User receiver, String file) {
		super(MessageType.SEND_REQUEST, origin, destination);
		this.receiver = receiver;
		this.file = file;
	}
	
	public User getReceiver() {
		return receiver;
	}
	
	public String getFile() {
		return file;
	}
}
