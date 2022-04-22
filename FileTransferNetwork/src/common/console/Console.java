package common.console;

import java.time.Instant;

public class Console {
	public static synchronized void print(String msg) {
		System.out.println(String.format("[%s]: %s",Instant.now().toString(), msg));
	}
}
