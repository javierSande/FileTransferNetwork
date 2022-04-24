/*
 * Programacion Concurrente - Practica Final
 * Curso 2021/22
 * Prof.: Elvira Albert Albiol
 * Alumnos: Javier Sande Rios, Mario Sanz Guerrero
 */

package common.messages;

public class DataMessage extends Message {

	private static final long serialVersionUID = 1L;
	private byte[] data; 
	private int size;

	public DataMessage(String origin, String destination, byte[] data, int size) {
		super(MessageType.DATA, origin, destination);
		this.data = data;
		this.size = size;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int getSize() {
		return size;
	}
}
