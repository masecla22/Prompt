package masecla.prompt.classes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestBuilder {
	private RequestType type;
	private String path;
	private String httpVer;
	private Map<String, ArrayList<String>> headers = new HashMap<>();
	private byte[] body;

	private RequestBuilder() {
	}

	public static RequestBuilder create() {
		return new RequestBuilder();
	}

	private static boolean checkEnding(List<Byte> bytes) {
		if (bytes.size() < 4)
			return false;
		int sPos = bytes.size();
		return bytes.get(sPos - 1) == 10 && bytes.get(sPos - 2) == 13 && bytes.get(sPos - 3) == 10
				&& bytes.get(sPos - 4) == 13; // Check if the stream is ending in \r\n\r\n
	}

	public static RequestBuilder create(InputStream stream) {
		List<Byte> list = new ArrayList<Byte>();
		RequestBuilder res = new RequestBuilder();
		while (!checkEnding(list)) {
			byte bt;
			try {
				bt = (byte) stream.read();
				list.add(bt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		byte[] unboxedHeadBytes = new byte[list.size()];
		for (int i = 0; i < list.size(); i++)
			unboxedHeadBytes[i] = list.get(i);

		// Gets the requestHead without the trailing \r\n\r\n
		String requestHead = new String(unboxedHeadBytes).substring(0, unboxedHeadBytes.length - 4);

		String[] lines = requestHead.split("\n");

		String requestInfo = lines[0];

		String[] requestInfoPieces = requestInfo.split(" ");
		res.type = RequestType.valueOf(requestInfoPieces[0]);
		res.path = requestInfoPieces[1];
		res.httpVer = requestInfoPieces[2];

		for (int i = 1; i < lines.length; i++) {
			String[] headerPieces = lines[i].split(":");
			String headerName = headerPieces[0];
			String headerValue = "";
			for (int j = 1; j < headerPieces.length; j++)
				headerValue += headerPieces[j] + ": ";
			headerValue = headerValue.substring(0, headerValue.length() - 2);

			res.addHeader(headerName, headerValue);
		}

		// The following methods have a body
		if (res.type.equals(RequestType.POST) || res.type.equals(RequestType.PUT) || res.type.equals(RequestType.DELETE)
				|| res.type.equals(RequestType.PATCH)) {
			List<String> sizes = res.headers.get("Content-Length");
			if (sizes.size() != 0) {
				try {
					int size = Integer.parseInt(sizes.get(0));

					// TODO, this is bad, do chunked reading
					byte[] bts = new byte[size];
					stream.read(bts);
					res.body = bts;

				} catch (IOException | NumberFormatException e) {
					// Invalid body size, or error reading, simply don't set the body
				}
			}
		}

		return res;
	}

	public RequestBuilder setType(RequestType type) {
		this.type = type;
		return this;
	}

	public RequestBuilder setPath(String path) {
		this.path = path;
		return this;
	}

	public RequestBuilder setHttpVer(String httpver) {
		this.httpVer = httpver;
		return this;
	}

	public RequestBuilder setBody(byte[] body) {
		this.body = body;
		return this;
	}
	
	public RequestBuilder setBody(String body) {
		this.body = body.getBytes();
		return this;
	}

	public RequestBuilder header(String key, String data) {
		ArrayList<String> res = new ArrayList<>();
		res.add(data);
		this.headers.put(data, res);
		return this;
	}

	public RequestBuilder addHeader(String key, String data) {
		ArrayList<String> res = this.headers.getOrDefault(key, new ArrayList<String>());
		res.add(data);
		this.headers.put(key, res);
		return this;
	}

	public Request build() {
		return new Request(type, path, httpVer, headers, body);
	}

}
