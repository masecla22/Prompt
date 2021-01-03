package masecla.prompt.classes;

public abstract class Webpage {

	private Website parent;

	public Webpage(Website parent) {
		super();
		this.parent = parent;
	}
	
	public Website getParent() {
		return parent;
	}

	public abstract void generateResponse(Request request, ResponseBuilder response);

}
