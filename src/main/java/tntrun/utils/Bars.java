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

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import tntrun.FormattingCodesParser;
import tntrun.TNTRun;

public class Bars {

	public static String waiting = "&6Waiting for more players, current player count:&r {COUNT}";
	public static String starting = "&6Arena starts in:&r {SECONDS} seconds";
	public static String playing = "&6Time left:&r {SECONDS} &6Players in game count:&r {COUNT}";

	public static void setBar(Player player, String message, int count, int seconds, float percent, TNTRun plugin) {
		try {
			message = message.replace("{COUNT}", String.valueOf(count));
			message = message.replace("{SECONDS}", String.valueOf(seconds));
			message = FormattingCodesParser.parseFormattingCodes(message);
			if(plugin.getConfig().getBoolean("special.UseBarApi") == false){
				return;
			}
			if (Bukkit.getPluginManager().getPlugin("BarAPI") != null) {
				if (!message.equals("")) {
					BarAPI.setMessage(player, message, percent);
				}
			}
		} catch (Throwable t) {
		}
	}

	public static void removeBar(Player player) {
		try {
			if (Bukkit.getPluginManager().getPlugin("BarAPI") != null) {
				BarAPI.removeBar(player);
			}
		} catch (Throwable t) {
		}
	}

	public static void loadBars(TNTRun plugin) {
		File messageconfig = new File(plugin.getDataFolder(), "configbars.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(messageconfig);
		waiting = config.getString("waiting", waiting);
		starting = config.getString("starting", starting);
		playing = config.getString("playing", playing);
		saveBars(messageconfig);
	}

	private static void saveBars(File messageconfig) {
		FileConfiguration config = new YamlConfiguration();
		config.set("waiting", waiting);
		config.set("starting", starting);
		config.set("playing", playing);
		try {
			config.save(messageconfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
