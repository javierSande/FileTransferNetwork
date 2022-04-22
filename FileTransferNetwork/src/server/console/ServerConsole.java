package server.console;

import java.time.Instant;

public class ServerConsole {
	public static synchronized void print(String msg) {
		System.out.println(String.format("[%s]: %s",Instant.now().toString(), msg));
	}
}
