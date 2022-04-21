package server.view;

import server.network.Server;

public interface Observable<T> {
	public void addObserver(Observer<T> o);

	public default void removeObserver(Observer<Server> o) {}
}
