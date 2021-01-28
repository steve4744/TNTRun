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

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import tntrun.TNTRun;
import tntrun.arena.Arena;

public class Utils {
	
	public static boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {}
        return false;
    }
	
	public static boolean isDouble(String text) {
		try {
			Double.parseDouble(text);
			return true;
		} catch (NumberFormatException e) {}
		return false;
	}
	
	public static int playerCount() {
		int pCount = 0;
		for (Arena arena : TNTRun.getInstance().amanager.getArenas()) {
			pCount += arena.getPlayersManager().getPlayersCount();			
		}
		return pCount;
	}
	
	public static void displayInfo(Player player) {
		player.sendMessage("§7============[§6TNTRun§7]§7============");
		player.sendMessage("§cVersion of plugin> §6" + TNTRun.getInstance().getDescription().getVersion());
		player.sendMessage("§cWebsite> §6https://www.spigotmc.org/resources/tntrun_reloaded.53359/");
		player.sendMessage("§cOriginal TNTRun Author> §6Shevchikden");
		player.sendMessage("§cTNTRun_reloaded Author> §6steve4744");
		player.sendMessage("§7============[§6TNTRun§7]§7============");
	}

	/**
	 * Attempt to get a player's rank. This can be either the player's prefix or primary group.
	 *
	 * @param player
	 * @return Player's rank.
	 */
	public static String getRank(OfflinePlayer player) {
		FileConfiguration config = TNTRun.getInstance().getConfig();
		if (player == null || !config.getBoolean("UseRankInChat.enabled")) {
			return "";
		}
		String rank = null;
		if (TNTRun.getInstance().getVaultHandler().isPermissions()) {
			if (config.getBoolean("UseRankInChat.usegroup")) {
				rank = TNTRun.getInstance().getVaultHandler().getPermissions().getPrimaryGroup(null, player);
				if (rank != null) {
					rank = "[" + rank + "]";
				}
			}
		}
		if (TNTRun.getInstance().getVaultHandler().isChat()) {
			if (config.getBoolean("UseRankInChat.useprefix")) {
				rank = TNTRun.getInstance().getVaultHandler().getChat().getPlayerPrefix(null, player);
			}
		}
		return rank == null ? "" : rank;
	}

	public static boolean debug() {
		return TNTRun.getInstance().getConfig().getBoolean("debug", false);
	}

}
