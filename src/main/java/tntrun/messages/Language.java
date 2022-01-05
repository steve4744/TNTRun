package tntrun.messages;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tntrun.TNTRun;

public class Language {

	private TNTRun plugin;
	public static final String PATH = "lang/"; 
	public static final String MSGFILE = "/messages.yml"; 

	public Language(TNTRun plugin) {
		this.plugin = plugin;
	}

	/**
	 * If no messages.yml file exists, then install the appropriate language version as specified in the config file.
	 *
	 * @param messageconfig
	 */
	public void updateLangFile(File messageconfig) {
		if (!messageconfig.exists()) {
			if (plugin.getResource(PATH + getLang() + MSGFILE) == null) {
				plugin.getLogger().info("Requested resource is not present: " + getLang());
				return;
			}
			if (!Files.isDirectory(plugin.getDataFolder().toPath())) {
				return;
			}
			try {
				Files.copy(plugin.getResource(PATH + getLang() + MSGFILE), new File(plugin.getDataFolder(), MSGFILE).toPath(), REPLACE_EXISTING);
			} catch (IOException e) {
				plugin.getLogger().info("Error copying file " + messageconfig);
				e.printStackTrace();
			}
		}
	}

	public String getLang() {
		return plugin.getConfig().getString("language", "en-GB");
	}

	public void setLang(String langDesc) {
		plugin.getConfig().set("language", getLangCode(langDesc));
		plugin.saveConfig();
	}

	/**
	 * Get the descriptive names of all the supported languages.
	 *
	 * @return List of supported languages
	 */
	public List<String> getTranslatedLanguages() {
		return Stream.of(EnumLang.values())
				.filter(EnumLang::isSupported)
				.map(EnumLang::getDesc)
				.collect(Collectors.toList());
	}

	/**
	 * Get the associated language code from the description.
	 *
	 * @param langDesc language descriptive name
	 * @return language code
	 */
	private String getLangCode(String langDesc) {
		return Stream.of(EnumLang.values())
			.filter(e -> e.getDesc().equals(langDesc))
			.map(EnumLang::getCode)
			.findFirst()
			.orElse("en-GB");
	}
}
