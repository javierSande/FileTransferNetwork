package common.messages;

public class FileRequestMessage extends Message {
	
	private static final long serialVersionUID = 1L;
	private String file;

	public FileRequestMessage(String origin, String destination, String file) {
		super(MessageType.FILE_REQUEST, origin, destination);
		this.file = file;
	}
	
	public String getFile() {
		return file;
	}
}