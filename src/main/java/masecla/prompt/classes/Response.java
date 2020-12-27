package masecla.prompt.classes;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class Response {
	private int statuscode;
	private String msgcode;

	private Map<String, ArrayList<String>> headers;

	private String body;

	public Response(int statuscode, String msgcode, Map<String, ArrayList<String>> headers, String body) {
		super();
		this.statuscode = statuscode;
		this.msgcode = msgcode;
		this.headers = headers;
		this.body = body;
	}

	public void applyLength() {
		ArrayList<String> header = new ArrayList<>();
		header.add(body.length() + "");
		this.headers.put("Content-Length", header);
	}

	public void sendToSocket(Socket sock) throws IOException {
		StringBuilder res = new StringBuilder("HTTP/1.1 " + statuscode + " " + msgcode.replace(" ", "_") + "\r\n");
		applyLength();
		headers.forEach((c, v) -> v.forEach(k -> res.append(c + ": " + k + "\r\n")));
		res.append("\r\n");
		res.append(body);

		System.out.println(res.toString());

		sock.getOutputStream().write(res.toString().getBytes());
	}

}
