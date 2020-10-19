package tntrun.messages;

public enum EnumLang {

	Arabic("ar", false),
	Czech("cs", false),
	Danish("da", false),
	Dutch("nl", false),
	Spanish("es", true),
	Spanish_Spain("es-ES", false),
	English("en-GB", true),
	English_US("en-US", false);
	
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
