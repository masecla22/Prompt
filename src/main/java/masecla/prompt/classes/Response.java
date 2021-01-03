package masecla.prompt.classes;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Response {
	private int statuscode = -1;
	private String msgcode;

	private Map<String, ArrayList<String>> headers = new HashMap<>();

	private byte[] body = new byte[0];

	public Response(int statuscode, String msgcode, Map<String, ArrayList<String>> headers, byte[] body) {
		super();
		this.statuscode = statuscode;
		this.msgcode = msgcode;
		this.headers = headers;
		this.body = body;
	}

	public void implyDefaults() {
		if (statuscode == -1)
			statuscode = 404;
		if (msgcode == null)
			msgcode = "NOT_FOUND";
		if (body == null)
			body = new byte[0];
		if (this.headers == null)
			this.headers = new HashMap<>();
		if (!this.headers.containsKey("Server"))
			header("Server", "Prompt");
		if (!this.headers.containsKey("Content-Type"))
			header("Content-Type", "text/html; charset=UTF-8");
		applyLength();
	}

	public void applyLength() {
		ArrayList<String> lengthHeader = new ArrayList<>();
		lengthHeader.add(body.length + "");
		this.headers.put("Content-Length", lengthHeader);
	}

	public void header(String key, String data) {
		ArrayList<String> res = new ArrayList<>();
		res.add(data);
		this.headers.put(key, res);
	}

	public void addHeader(String key, String data) {
		ArrayList<String> res = this.headers.getOrDefault(key, new ArrayList<String>());
		res.add(data);
		this.headers.put(key, res);
	}

	public void sendToSocket(Socket sock) throws IOException {
		implyDefaults();
		StringBuilder res = new StringBuilder("HTTP/1.1 " + statuscode + " " + msgcode.replace(" ", "_") + "\r\n");
		headers.forEach((c, v) -> v.forEach(k -> res.append(c + ": " + k + "\r\n")));
		res.append("\r\n");

		System.out.println(res.toString());

		sock.getOutputStream().write(res.toString().getBytes());
		sock.getOutputStream().write(body);
	}

}
