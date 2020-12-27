package masecla.prompt.classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Website {
	private int port = 80;

	private ServerSocket socket;
	private Thread connectionAcceptingThread;

	private AtomicBoolean running = new AtomicBoolean(false);

	public Website() {
		super();
	}

	public void open() throws IOException {
		this.socket = new ServerSocket(port);
		this.running.set(true);
	}

	public void close() throws IOException, InterruptedException {
		close(-1);
	}

	public void close(int millis) throws IOException, InterruptedException {
		this.running.set(false);
		this.socket.close();
		this.connectionAcceptingThread.join(millis);

	}

}
