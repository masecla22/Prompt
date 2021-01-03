package masecla.prompt.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {
	private int statuscode;
	private String msgcode;

	private Map<String, ArrayList<String>> headers = new HashMap<>();

	private byte[] body;

	private ResponseBuilder() {
	}

	public static ResponseBuilder create() {
		return new ResponseBuilder();
	}

	public ResponseBuilder setStatusCode(int code) {
		this.statuscode = code;
		return this;
	}

	public ResponseBuilder setMessageCode(String message) {
		this.msgcode = message;
		return this;
	}

	public ResponseBuilder setBody(byte[] body) {
		this.body = body;
		return this;
	}

	public ResponseBuilder setBody(String body) {
		this.body = body.getBytes();
		return this;
	}

	public ResponseBuilder header(String key, String data) {
		ArrayList<String> res = new ArrayList<>();
		res.add(data);
		this.headers.put(key, res);
		return this;
	}

	public ResponseBuilder addHeader(String key, String data) {
		ArrayList<String> res = this.headers.getOrDefault(key, new ArrayList<String>());
		res.add(data);
		this.headers.put(key, res);
		return this;
	}

	public Response build() {
		return new Response(statuscode, msgcode, headers, body);
	}

}
