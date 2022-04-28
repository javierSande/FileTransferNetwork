package common.monitors;

public interface Monitor {
	
	public void startRead();
	public void endRead();
	public void startWrite();
	public void endWrite();
	
}
