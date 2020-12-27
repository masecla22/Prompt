package masecla.prompt.classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Website {
	private int port = 80;

	private ServerSocket socket;
	private Thread connectionAcceptingThread;

	private AtomicBoolean running = new AtomicBoolean(false);

	private Map<Socket, Thread> activeConnections = new LinkedHashMap<>();

	public Website() {
		super();
	}

	public void open() throws IOException {
		this.socket = new ServerSocket(port);
		this.running.set(true);

		this.connectionAcceptingThread = new Thread(() -> {
			while (running.get()) {
				try {
					Socket client = socket.accept();
					Thread clientThread = new Thread(() -> {
						
					});
					clientThread.start();
					this.activeConnections.put(client, clientThread);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		this.connectionAcceptingThread.start();
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
