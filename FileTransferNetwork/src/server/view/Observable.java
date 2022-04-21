package server.view;

public interface Observable<T> {
	public void addObserver(Observer<T> o);

	public default void removeObserver(Observer<T> o) {}
}
