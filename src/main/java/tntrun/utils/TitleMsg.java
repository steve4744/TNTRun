/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tntrun.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import tntrun.TNTRun;

public class TitleMsg {

	public static String join = "&7[&6TNTRun&7]";
	public static String subjoin = "&6{PLAYER} &7joined";
	public static String win = "&6You won";
	public static String subwin = "&7Congratulations";
	public static String starting = "&7[&6TNTRun&7]";
	public static String substarting = "&7Starting in &6{COUNT}";
	public static String start = "&7[&6TNTRun&7]";
	public static String substart = "&7The Game has started";

	public static void sendFullTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, TNTRun plugin) {
		if (!plugin.getConfig().getBoolean("special.UseTitle")) {
			return;
		}
		player.sendTitle(FormattingCodesParser.parseFormattingCodes(title), FormattingCodesParser.parseFormattingCodes(subtitle), fadeIn, stay, fadeOut);
	}

	public static void loadTitles(TNTRun plugin) {
		File messageconfig = new File(plugin.getDataFolder(), "configtitles.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(messageconfig);
		join = config.getString("join", join);
		subjoin = config.getString("subjoin", subjoin);
		win = config.getString("win", win);
		subwin = config.getString("subwin", subwin);
		starting = config.getString("starting", starting);
		substarting = config.getString("substarting", substarting);
		start = config.getString("start", start);
		substart = config.getString("substart", substart);
		saveTitles(messageconfig);
	}

	private static void saveTitles(File messageconfig) {
		FileConfiguration config = new YamlConfiguration();
		config.set("join", join);
		config.set("subjoin", subjoin);
		config.set("win", win);
		config.set("subwin", subwin);
		config.set("starting", starting);
		config.set("substarting", substarting);
		config.set("start", start);
		config.set("substart", substart);
		try {
			config.save(messageconfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
