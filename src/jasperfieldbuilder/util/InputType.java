package jasperfieldbuilder.util;

public enum InputType {
	PARAMETER ("parameter"),
	FIELD ("field");
	
	private final String description;
	
	InputType (String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
