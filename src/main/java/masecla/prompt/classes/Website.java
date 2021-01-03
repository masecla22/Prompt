package masecla.prompt.classes;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Website {
	private int port = 80;

	private ServerSocket socket;
	private Thread connectionAcceptingThread;

	private AtomicBoolean running = new AtomicBoolean(false);

	private Map<Socket, Thread> activeConnections = new LinkedHashMap<>();

	private File homeDirectory = new File(".");

	public Website() {
		super();
		this.notFoundPage = new Webpage(this) {
			@Override
			public void generateResponse(Request request, ResponseBuilder response) {
				response.setStatusCode(404);
			}
		};
	}

	private Webpage notFoundPage;

	public void setNotFoundPage(Webpage notFoundPage) {
		this.notFoundPage = notFoundPage;
	}
	
	public void map(String url, Webpage wb) {
		this.mappings.put(url, wb);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setHomeDirectory(File homeDirectory) {
		this.homeDirectory = homeDirectory;
	}

	public File getHomeDirectory() {
		return homeDirectory;
	}

	public int getPort() {
		return port;
	}

	public void open() throws IOException {
		this.socket = new ServerSocket(port);
		this.running.set(true);

		this.connectionAcceptingThread = new Thread(() -> {
			while (running.get()) {
				try {
					Socket client = socket.accept();
					Thread clientThread = new Thread(() -> {
						try {
							Request req = RequestBuilder.create(client.getInputStream()).build();
							Response rsp = this.queryResponse(req);
							rsp.sendToSocket(client);
							client.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
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

	private Map<String, Webpage> mappings = new HashMap<>();

	private Response queryResponse(Request req) {
		String url = req.getPath().split("\\?")[0]; // Strip the parameters
		ResponseBuilder builder = ResponseBuilder.create();
		if (mappings.containsKey(url)) {
			Webpage page = mappings.get(url);
			page.generateResponse(req, builder);
			builder.setStatusCode(200);
			builder.setMessageCode("OK");
			
		} else {
			File f = new File(this.homeDirectory, url);
			if (f.exists() && !f.isDirectory()) {
				try {
					byte[] bts = Files.readAllBytes(f.toPath());
					builder.setBody(bts);
					builder.header("Content-Type", new CreateMIMEType(f).getMIMEText());
					builder.setStatusCode(200);
					builder.setMessageCode("OK");
				} catch (IOException e) {
				}
			} else {
				this.notFoundPage.generateResponse(req, builder);
				builder.setStatusCode(404);
			}
		}
		return builder.build();
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
