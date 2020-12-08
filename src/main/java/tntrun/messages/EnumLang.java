package tntrun.messages;

public enum EnumLang {

	Arabic("ar", false),
	Czech("cs", false),
	Danish("da", false),
	Dutch("nl", false),
	English("en-GB", true),
	English_US("en-US", false),
	Finnish("fi", false),
	French("fr", false),
	German("de", false),
	Italian("it", false),
	Japanese("ja", false),
	Korean("ko", false),
	Norwegian("no", false),
	Polish("pl", false),
	Portugese("pt", false),
	Russian("ru", false),
	Spanish("es", true),
	Swedish("sv", false),
	Welsh("cy", true);
	
	private final String name;
	private final boolean supported;

	EnumLang(String name, boolean supported) {
		this.name = name;
		this.supported = supported;
	}

	public boolean isSupported() {
		return supported;
	}

	public String getName() {
		return name;
	}
}
