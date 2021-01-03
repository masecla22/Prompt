package masecla.prompt.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Request {
	private RequestType type;
	private String path;
	private String httpVer;
	private Map<String, ArrayList<String>> headers = new HashMap<>();
	private byte[] body;

	public Request(RequestType type, String path, String httpVer, Map<String, ArrayList<String>> headers, byte[] body) {
		super();
		this.type = type;
		this.path = path;
		this.httpVer = httpVer;
		this.headers = headers;
		this.body = body;
	}

	public byte[] getBody() {
		return body;
	}

	public Map<String, ArrayList<String>> getHeaders() {
		return headers;
	}

	public String getHttpVer() {
		return httpVer;
	}

	public String getPath() {
		return path;
	}

	public RequestType getType() {
		return type;
	}

}
