package common.messages;

public enum MessageType {
	
	CONNECTION, CONFIRM_CONNECTION, 
	USER_LIST, CONFIRM_USER_LIST,
	FILE_REQUEST,
	SEND_REQUEST, 
	CLIENT_SERVER_READY,
	SERVER_CLIENT_READY,
	TERMINATE, CONFIRM_TERMINATE,
	ERROR,
	DATA,
	USER_UPDATE,
	SERVER_UPDATE,
	EOF

}
