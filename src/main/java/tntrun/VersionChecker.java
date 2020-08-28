package tntrun;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bukkit.Bukkit;

public class VersionChecker {

	private static VersionChecker instance;

	public VersionChecker() {
		instance = this;
	}

	public static VersionChecker get() {
		return instance;
	}

	public String getVersion() {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=53359").openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("GET");
			String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			con.disconnect();
			if (version.length() <= 7) {
				return version;
			}
		} catch (Exception ex) {
			Bukkit.getLogger().info("[TNTRun_reloaded] Failed to check for update on Spigot");
		}
		return "error";
	}
}
